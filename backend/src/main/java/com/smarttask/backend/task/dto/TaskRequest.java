package com.smarttask.backend.task.dto;

import com.smarttask.backend.task.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record TaskRequest(
        @NotBlank @Size(min = 3, max = 200) String title,
        String description,
        @NotNull UUID projectId,
        UUID assigneeId,
        TaskPriority priority,
        Instant dueDate
) {}
