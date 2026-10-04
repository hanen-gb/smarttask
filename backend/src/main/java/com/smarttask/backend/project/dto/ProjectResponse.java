package com.smarttask.backend.project.dto;

import com.smarttask.backend.project.ProjectStatus;

import java.time.Instant;
import java.time.LocalDate;

public record ProjectResponse(
        String id,
        String name,
        String description,
        ProjectStatus status,
        String color,
        LocalDate startDate,
        LocalDate endDate,
        OwnerSummary owner,
        int memberCount,
        Instant createdAt
) {
    public record OwnerSummary(String id, String firstName, String lastName) {}
}
