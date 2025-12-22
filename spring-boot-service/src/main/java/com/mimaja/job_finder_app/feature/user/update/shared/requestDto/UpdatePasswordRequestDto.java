package com.mimaja.job_finder_app.feature.user.update.shared.requestDto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequestDto(@NotBlank String password, @NotBlank String newPassword) {}
