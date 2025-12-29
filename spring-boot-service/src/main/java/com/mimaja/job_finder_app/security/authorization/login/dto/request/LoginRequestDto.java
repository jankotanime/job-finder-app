package com.mimaja.job_finder_app.security.authorization.login.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(@NotBlank String loginData, @NotBlank String password) {}
