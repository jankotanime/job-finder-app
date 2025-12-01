package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestPasswordUpdateEmailRequestDto(@NotBlank String loginData) {}
