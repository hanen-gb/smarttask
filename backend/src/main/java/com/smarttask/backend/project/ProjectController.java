package com.smarttask.backend.project;

import com.smarttask.backend.project.dto.AddMemberRequest;
import com.smarttask.backend.project.dto.ProjectRequest;
import com.smarttask.backend.project.dto.ProjectResponse;
import com.smarttask.backend.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<ProjectResponse> getMyProjects(@AuthenticationPrincipal User currentUser) {
        return projectService.getMyProjects(currentUser);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ProjectRequest request
    ) {
        return projectService.createProject(currentUser, request);
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        return projectService.getProjectById(id, currentUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        projectService.deleteProject(id, currentUser);
    }

    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public void addMember(
            @PathVariable UUID id,
            @Valid @RequestBody AddMemberRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        projectService.addMember(id, request.userId(), currentUser);
    }
}
