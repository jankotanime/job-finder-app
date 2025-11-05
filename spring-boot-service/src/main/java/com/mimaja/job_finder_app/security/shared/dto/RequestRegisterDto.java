package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotNull;

public record RequestRegisterDto(
        @NotNull String username,
        @NotNull String email,
        @NotNull String phoneNumber,
        @NotNull String password) {}
