package com.smarttask.backend.auth.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    UserSummary user
) {
    public record UserSummary(
        String id,
        String email,
        String firstName,
        String lastName,
        String role
    ) {}
}
