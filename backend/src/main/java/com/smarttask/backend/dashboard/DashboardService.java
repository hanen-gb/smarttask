package com.smarttask.backend.dashboard;

import com.smarttask.backend.dashboard.dto.DashboardSummaryResponse;
import com.smarttask.backend.project.Project;
import com.smarttask.backend.project.ProjectMemberRepository;
import com.smarttask.backend.project.ProjectRepository;
import com.smarttask.backend.project.ProjectStatus;
import com.smarttask.backend.task.Task;
import com.smarttask.backend.task.TaskPriority;
import com.smarttask.backend.task.TaskRepository;
import com.smarttask.backend.task.TaskStatus;
import com.smarttask.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;

    public DashboardSummaryResponse getSummary(User currentUser) {
        List<UUID> myProjectIds = myProjectIds(currentUser);
        List<Project> myProjects = projectRepository.findAllById(myProjectIds);
        List<Task> myTasks = taskRepository.findByProjectIdIn(myProjectIds);

        long activeProjects = myProjects.stream().filter(p -> p.getStatus() == ProjectStatus.ACTIVE).count();

        long myOpenTasks = myTasks.stream()
                .filter(t -> isMine(t, currentUser))
                .filter(t -> !isFinished(t))
                .count();

        Instant now = Instant.now();
        long myOverdueTasks = myTasks.stream()
                .filter(t -> isMine(t, currentUser))
                .filter(t -> !isFinished(t))
                .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(now))
                .count();

        Instant weekAgo = now.minus(7, ChronoUnit.DAYS);
        long completedThisWeek = myTasks.stream()
                .filter(t -> t.getCompletedAt() != null && t.getCompletedAt().isAfter(weekAgo))
                .count();

        long totalTasks = myTasks.size();
        long doneTasks = myTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        double completionRate = totalTasks == 0 ? 0.0 : (double) doneTasks / totalTasks;

        return new DashboardSummaryResponse(
                myProjects.size(), activeProjects, totalTasks,
                myOpenTasks, myOverdueTasks, completedThisWeek, completionRate
        );
    }

    public Map<TaskStatus, Long> getTasksByStatus(User currentUser) {
        List<Task> myTasks = taskRepository.findByProjectIdIn(myProjectIds(currentUser));

        Map<TaskStatus, Long> counts = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) {
            counts.put(status, 0L);
        }
        myTasks.forEach(t -> counts.merge(t.getStatus(), 1L, Long::sum));
        return counts;
    }

    public Map<TaskPriority, Long> getTasksByPriority(User currentUser) {
        List<Task> myTasks = taskRepository.findByProjectIdIn(myProjectIds(currentUser));

        Map<TaskPriority, Long> counts = new EnumMap<>(TaskPriority.class);
        for (TaskPriority priority : TaskPriority.values()) {
            counts.put(priority, 0L);
        }
        myTasks.forEach(t -> counts.merge(t.getPriority(), 1L, Long::sum));
        return counts;
    }

    private List<UUID> myProjectIds(User currentUser) {
        return projectMemberRepository.findByUserId(currentUser.getId()).stream()
                .map(m -> m.getProject().getId())
                .toList();
    }

    private boolean isMine(Task task, User user) {
        return task.getAssignee() != null && task.getAssignee().getId().equals(user.getId());
    }

    private boolean isFinished(Task task) {
        return task.getStatus() == TaskStatus.DONE || task.getStatus() == TaskStatus.CANCELLED;
    }
}
