package com.smarttask.backend.project.dto;

import com.smarttask.backend.project.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProjectRequest(
        @NotBlank @Size(min = 3, max = 120) String name,
        String description,
        ProjectStatus status,
        String color,
        LocalDate startDate,
        LocalDate endDate
) {}
