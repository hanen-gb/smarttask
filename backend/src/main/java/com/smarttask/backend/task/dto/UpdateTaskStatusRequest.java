package com.smarttask.backend.task.dto;

import com.smarttask.backend.task.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(@NotNull TaskStatus status) {}
