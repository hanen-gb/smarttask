package com.smarttask.backend.user.dto;

import com.smarttask.backend.user.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(
    @NotNull Role role
) {}
