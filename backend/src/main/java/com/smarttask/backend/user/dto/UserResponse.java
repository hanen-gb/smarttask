package com.smarttask.backend.user.dto;

import com.smarttask.backend.user.Role;

public record UserResponse(
    String id,
    String email,
    String firstName,
    String lastName,
    Role role,
    boolean enabled,
    String avatarUrl
) {}
