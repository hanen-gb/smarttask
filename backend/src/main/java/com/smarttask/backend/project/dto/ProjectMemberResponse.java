package com.smarttask.backend.project.dto;

import com.smarttask.backend.project.ProjectRole;

public record ProjectMemberResponse(
        String userId,
        String firstName,
        String lastName,
        String email,
        ProjectRole projectRole,
        boolean enabled
) {}
