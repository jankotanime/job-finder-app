package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestRegisterDto(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String phoneNumber,
        @NotBlank String password) {}
