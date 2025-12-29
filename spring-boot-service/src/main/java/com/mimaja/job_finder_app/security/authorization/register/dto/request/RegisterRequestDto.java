package com.mimaja.job_finder_app.security.authorization.register.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank int phoneNumber,
        @NotBlank String password) {}
