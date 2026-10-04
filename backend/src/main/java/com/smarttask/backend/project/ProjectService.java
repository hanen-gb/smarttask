package com.smarttask.backend.project;

import com.smarttask.backend.project.dto.ProjectRequest;
import com.smarttask.backend.project.dto.ProjectResponse;
import com.smarttask.backend.user.Role;
import com.smarttask.backend.user.User;
import com.smarttask.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public ProjectResponse createProject(User currentUser, ProjectRequest request) {
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setColor(request.color());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setOwner(currentUser);

        Project saved = projectRepository.save(project);

        // Regle R4 : le createur devient automatiquement membre avec le role OWNER
        ProjectMember membership = new ProjectMember();
        membership.setProject(saved);
        membership.setUser(currentUser);
        membership.setProjectRole(ProjectRole.OWNER);
        projectMemberRepository.save(membership);

        return toResponse(saved);
    }

    public List<ProjectResponse> getMyProjects(User currentUser) {
        return projectMemberRepository.findByUserId(currentUser.getId()).stream()
                .map(ProjectMember::getProject)
                .map(this::toResponse)
                .toList();
    }

    public ProjectResponse getProjectById(UUID projectId, User currentUser) {
        Project project = findProjectOrThrow(projectId);
        requireMembership(projectId, currentUser);
        return toResponse(project);
    }

    public void deleteProject(UUID projectId, User currentUser) {
        Project project = findProjectOrThrow(projectId);

        boolean isOwner = project.getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        // Regle R6 : seul le proprietaire ou un ADMIN peut supprimer
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seul le proprietaire ou un administrateur peut supprimer ce projet");
        }

        projectRepository.delete(project);
        // Les ProjectMember sont supprimes automatiquement par la base
        // (ON DELETE CASCADE dans la migration V2)
    }

    public void addMember(UUID projectId, UUID userId, User currentUser) {
        findProjectOrThrow(projectId);
        requireOwnerOrManager(projectId, currentUser);

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet utilisateur est deja membre du projet");
        }

        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        ProjectMember membership = new ProjectMember();
        membership.setProject(projectRepository.getReferenceById(projectId));
        membership.setUser(userToAdd);
        membership.setProjectRole(ProjectRole.CONTRIBUTOR);
        projectMemberRepository.save(membership);
    }

    // ---- Controles d'autorisation CONTEXTUELS (niveau 2, voir docs/01-ARCHITECTURE.md) ----

    private void requireMembership(UUID projectId, User currentUser) {
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isMember && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'etes pas membre de ce projet");
        }
    }

    private void requireOwnerOrManager(UUID projectId, User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        ProjectMember membership = projectMemberRepository.findByProjectIdAndUserId(projectId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'etes pas membre de ce projet"));

        boolean allowed = membership.getProjectRole() == ProjectRole.OWNER
                || membership.getProjectRole() == ProjectRole.MANAGER;

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Role insuffisant dans ce projet");
        }
    }

    private Project findProjectOrThrow(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet introuvable"));
    }

    private ProjectResponse toResponse(Project project) {
        int memberCount = projectMemberRepository.findByProjectId(project.getId()).size();
        return new ProjectResponse(
                project.getId().toString(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getColor(),
                project.getStartDate(),
                project.getEndDate(),
                new ProjectResponse.OwnerSummary(
                        project.getOwner().getId().toString(),
                        project.getOwner().getFirstName(),
                        project.getOwner().getLastName()
                ),
                memberCount,
                project.getCreatedAt()
        );
    }
}
