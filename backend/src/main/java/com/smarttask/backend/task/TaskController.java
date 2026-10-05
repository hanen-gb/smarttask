package com.smarttask.backend.task;

import com.smarttask.backend.common.dto.PageResponse;
import com.smarttask.backend.task.dto.TaskRequest;
import com.smarttask.backend.task.dto.TaskResponse;
import com.smarttask.backend.task.dto.UpdateTaskAssigneeRequest;
import com.smarttask.backend.task.dto.UpdateTaskRequest;
import com.smarttask.backend.task.dto.UpdateTaskStatusRequest;
import com.smarttask.backend.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/api/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@AuthenticationPrincipal User currentUser, @Valid @RequestBody TaskRequest request) {
        return taskService.createTask(currentUser, request);
    }

    @GetMapping("/api/tasks")
    public PageResponse<TaskResponse> searchTasks(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID assigneeId,
            @RequestParam(required = false, defaultValue = "false") boolean unassigned,
            @RequestParam(required = false) List<TaskStatus> status,
            @RequestParam(required = false) List<TaskPriority> priority,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dueBefore,
            Pageable pageable
    ) {
        return taskService.searchTasks(currentUser, projectId, assigneeId, unassigned, status, priority, search, dueBefore, pageable);
    }

    @GetMapping("/api/tasks/{id}")
    public TaskResponse getTaskById(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        return taskService.getTaskById(id, currentUser);
    }

    @PutMapping("/api/tasks/{id}")
    public TaskResponse updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return taskService.updateTask(id, currentUser, request);
    }

    @GetMapping("/api/projects/{projectId}/tasks")
    public List<TaskResponse> getProjectTasks(@PathVariable UUID projectId, @AuthenticationPrincipal User currentUser) {
        return taskService.getProjectTasks(projectId, currentUser);
    }

    @GetMapping("/api/projects/{projectId}/board")
    public Map<TaskStatus, List<TaskResponse>> getBoard(@PathVariable UUID projectId, @AuthenticationPrincipal User currentUser) {
        return taskService.getBoard(projectId, currentUser);
    }

    @PatchMapping("/api/tasks/{id}/status")
    public TaskResponse updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return taskService.updateStatus(id, request.status(), currentUser);
    }

    @PatchMapping("/api/tasks/{id}/assignee")
    public TaskResponse updateAssignee(
            @PathVariable UUID id,
            @RequestBody UpdateTaskAssigneeRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return taskService.updateAssignee(id, request.assigneeId(), currentUser);
    }

    @DeleteMapping("/api/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        taskService.deleteTask(id, currentUser);
    }
}
