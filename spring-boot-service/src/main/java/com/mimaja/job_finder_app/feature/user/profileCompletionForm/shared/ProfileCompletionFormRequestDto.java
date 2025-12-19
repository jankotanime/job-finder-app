package com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared;

import jakarta.validation.constraints.NotBlank;

public record ProfileCompletionFormRequestDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String profileDescription) {}
