package com.smarttask.backend.task.dto;

import com.smarttask.backend.task.TaskPriority;
import com.smarttask.backend.task.TaskStatus;

import java.time.Instant;

public record TaskResponse(
        String id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Instant dueDate,
        int position,
        String projectId,
        String projectName,
        UserRef assignee,
        UserRef reporter,
        Instant completedAt,
        Instant createdAt
) {
    public record UserRef(String id, String firstName, String lastName) {}
}
