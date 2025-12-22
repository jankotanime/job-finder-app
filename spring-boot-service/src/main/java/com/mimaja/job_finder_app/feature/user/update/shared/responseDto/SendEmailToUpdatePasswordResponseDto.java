package com.mimaja.job_finder_app.feature.user.update.shared.responseDto;

import jakarta.validation.constraints.NotBlank;

public record SendEmailToUpdatePasswordResponseDto(@NotBlank String email) {}
