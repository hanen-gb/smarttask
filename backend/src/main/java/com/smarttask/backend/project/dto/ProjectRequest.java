package com.smarttask.backend.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProjectRequest(
        @NotBlank @Size(min = 3, max = 120) String name,
        String description,
        String color,
        LocalDate startDate,
        LocalDate endDate
) {}
