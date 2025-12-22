package com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthLoginRequestDto(@NotBlank String googleToken, int smsCode) {}
