package com.mimaja.job_finder_app.feature.user.update.shared.requestDto;

import jakarta.validation.constraints.NotBlank;

public record SendEmailToUpdatePasswordRequestDto(@NotBlank String loginData) {}
