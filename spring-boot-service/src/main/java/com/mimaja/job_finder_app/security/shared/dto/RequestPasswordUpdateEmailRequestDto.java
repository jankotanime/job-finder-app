package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotNull;

public record RequestPasswordUpdateEmailRequestDto(@NotNull String loginData) {}
