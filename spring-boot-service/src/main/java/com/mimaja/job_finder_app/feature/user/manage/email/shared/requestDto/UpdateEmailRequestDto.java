package com.mimaja.job_finder_app.feature.user.manage.email.shared.requestDto;

import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequestDto(@NotBlank String newEmail, @NotBlank String password) {}
