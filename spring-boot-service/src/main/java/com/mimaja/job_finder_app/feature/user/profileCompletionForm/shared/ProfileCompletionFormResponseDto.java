package com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared;

import jakarta.validation.constraints.NotBlank;

public record ProfileCompletionFormResponseDto(@NotBlank String accessToken) {}
