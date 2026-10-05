package com.smarttask.backend.task.dto;

import com.smarttask.backend.task.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateTaskRequest(
        @NotBlank @Size(min = 3, max = 200) String title,
        String description,
        TaskPriority priority,
        Instant dueDate
) {}
