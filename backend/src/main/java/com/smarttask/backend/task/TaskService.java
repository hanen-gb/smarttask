package com.smarttask.backend.task;

import com.smarttask.backend.common.dto.PageResponse;
import com.smarttask.backend.notification.event.TaskAssignedEvent;
import com.smarttask.backend.project.Project;
import com.smarttask.backend.project.ProjectMemberRepository;
import com.smarttask.backend.project.ProjectRepository;
import com.smarttask.backend.project.ProjectRole;
import com.smarttask.backend.task.dto.TaskRequest;
import com.smarttask.backend.task.dto.TaskResponse;
import com.smarttask.backend.task.dto.UpdateTaskRequest;
import com.smarttask.backend.user.Role;
import com.smarttask.backend.user.User;
import com.smarttask.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TaskResponse createTask(User currentUser, TaskRequest request) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet introuvable"));

        requireMembership(request.projectId(), currentUser);

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setProject(project);
        task.setReporter(currentUser);
        task.setPriority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM);
        task.setDueDate(request.dueDate());

        if (request.assigneeId() != null) {
            task.setAssignee(resolveAssignee(request.projectId(), request.assigneeId()));
        }

        Task saved = taskRepository.save(task);

        if (saved.getAssignee() != null) {
            eventPublisher.publishEvent(new TaskAssignedEvent(saved, currentUser));
        }

        return toResponse(saved);
    }

    public TaskResponse getTaskById(UUID taskId, User currentUser) {
        Task task = findTaskOrThrow(taskId);
        requireMembership(task.getProject().getId(), currentUser);
        return toResponse(task);
    }

    public TaskResponse updateTask(UUID taskId, User currentUser, UpdateTaskRequest request) {
        Task task = findTaskOrThrow(taskId);
        requireCanModify(task, currentUser);

        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        task.setDueDate(request.dueDate());

        return toResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getProjectTasks(UUID projectId, User currentUser) {
        requireMembership(projectId, currentUser);
        return taskRepository.findByProjectIdOrderByPositionAsc(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    public Map<TaskStatus, List<TaskResponse>> getBoard(UUID projectId, User currentUser) {
        requireMembership(projectId, currentUser);
        List<TaskResponse> tasks = taskRepository.findByProjectIdOrderByPositionAsc(projectId).stream()
                .map(this::toResponse)
                .toList();
        return tasks.stream().collect(Collectors.groupingBy(TaskResponse::status));
    }

    /**
     * Recherche combinee (etape 8) : assemble dynamiquement une requete SQL a
     * partir de criteres optionnels. Chaque parametre null/vide est simplement
     * ignore -- on ne combine que les filtres reellement demandes.
     */
    public PageResponse<TaskResponse> searchTasks(
            User currentUser,
            UUID projectId,
            UUID assigneeId,
            boolean unassignedOnly,
            List<TaskStatus> statuses,
            List<TaskPriority> priorities,
            String search,
            Instant dueBefore,
            Pageable pageable
    ) {
        List<Specification<Task>> specs = new ArrayList<>();

        // Un non-ADMIN ne voit que les taches des projets dont il est membre
        if (currentUser.getRole() != Role.ADMIN) {
            List<UUID> myProjectIds = projectMemberRepository.findByUserId(currentUser.getId()).stream()
                    .map(m -> m.getProject().getId())
                    .toList();
            specs.add(TaskSpecifications.inProjects(myProjectIds));
        }

        if (projectId != null) specs.add(TaskSpecifications.projectIdEquals(projectId));
        if (unassignedOnly) specs.add(TaskSpecifications.unassigned());
        else if (assigneeId != null) specs.add(TaskSpecifications.assigneeIdEquals(assigneeId));
        if (statuses != null && !statuses.isEmpty()) specs.add(TaskSpecifications.statusIn(statuses));
        if (priorities != null && !priorities.isEmpty()) specs.add(TaskSpecifications.priorityIn(priorities));
        if (search != null && !search.isBlank()) specs.add(TaskSpecifications.titleContains(search));
        if (dueBefore != null) specs.add(TaskSpecifications.dueBefore(dueBefore));

        Specification<Task> combined = specs.stream()
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse(null);

        return PageResponse.from(taskRepository.findAll(combined, pageable).map(this::toResponse));
    }

    public TaskResponse updateStatus(UUID taskId, TaskStatus newStatus, User currentUser) {
        Task task = findTaskOrThrow(taskId);
        requireCanModify(task, currentUser);

        task.setStatus(newStatus);
        // Regle R10 : DONE renseigne completedAt, en sortir le remet a null
        task.setCompletedAt(newStatus == TaskStatus.DONE ? Instant.now() : null);

        return toResponse(taskRepository.save(task));
    }

    public TaskResponse updateAssignee(UUID taskId, UUID newAssigneeId, User currentUser) {
        Task task = findTaskOrThrow(taskId);
        // Regle plus stricte que requireCanModify() : un simple MEMBER (meme
        // rapporteur de la tache) ne peut PAS assigner une tache a quelqu'un
        // d'autre -- seuls OWNER/MANAGER du projet ou un ADMIN le peuvent.
        requireOwnerOrManagerOfProject(task.getProject().getId(), currentUser);

        task.setAssignee(newAssigneeId != null
                ? resolveAssignee(task.getProject().getId(), newAssigneeId)
                : null);

        Task saved = taskRepository.save(task);

        if (saved.getAssignee() != null) {
            eventPublisher.publishEvent(new TaskAssignedEvent(saved, currentUser));
        }

        return toResponse(saved);
    }

    public void deleteTask(UUID taskId, User currentUser) {
        Task task = findTaskOrThrow(taskId);
        requireCanModify(task, currentUser);
        taskRepository.delete(task);
    }

    // ---- Autorisations contextuelles ----

    private void requireMembership(UUID projectId, User currentUser) {
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember && currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'etes pas membre de ce projet");
        }
    }

    // Regle R11 : assignee, reporter, manager/owner du projet, ou ADMIN
    private void requireCanModify(Task task, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) return;

        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId());
        boolean isReporter = task.getReporter().getId().equals(currentUser.getId());

        if (isAssignee || isReporter) return;

        boolean isProjectManager = projectMemberRepository
                .findByProjectIdAndUserId(task.getProject().getId(), currentUser.getId())
                .map(m -> m.getProjectRole() == ProjectRole.OWNER || m.getProjectRole() == ProjectRole.MANAGER)
                .orElse(false);

        if (!isProjectManager) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez pas modifier cette tache");
        }
    }

    private void requireOwnerOrManagerOfProject(UUID projectId, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) return;

        boolean allowed = projectMemberRepository.findByProjectIdAndUserId(projectId, currentUser.getId())
                .map(m -> m.getProjectRole() == ProjectRole.OWNER || m.getProjectRole() == ProjectRole.MANAGER)
                .orElse(false);

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seul le proprietaire/manager du projet peut assigner cette tache");
        }
    }

    // Regle R9 : on ne peut assigner qu'a un membre du projet
    private User resolveAssignee(UUID projectId, UUID userId) {
        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet utilisateur n'est pas membre du projet");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    private Task findTaskOrThrow(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tache introuvable"));
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId().toString(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getPosition(),
                task.getProject().getId().toString(),
                task.getProject().getName(),
                task.getAssignee() != null
                        ? new TaskResponse.UserRef(task.getAssignee().getId().toString(), task.getAssignee().getFirstName(), task.getAssignee().getLastName())
                        : null,
                new TaskResponse.UserRef(task.getReporter().getId().toString(), task.getReporter().getFirstName(), task.getReporter().getLastName()),
                task.getCompletedAt(),
                task.getCreatedAt()
        );
    }
}
