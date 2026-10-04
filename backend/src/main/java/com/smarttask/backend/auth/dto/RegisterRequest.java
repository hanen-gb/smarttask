package com.smarttask.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Email String email,

    @NotBlank
    @Size(min = 8, message = "le mot de passe doit contenir au moins 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
        message = "le mot de passe doit contenir une majuscule, une minuscule et un chiffre"
    )
    String password,

    @NotBlank String firstName,
    @NotBlank String lastName
) {}
