package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotNull;

public record RequestLoginDto(@NotNull String loginData, @NotNull String password) {}
