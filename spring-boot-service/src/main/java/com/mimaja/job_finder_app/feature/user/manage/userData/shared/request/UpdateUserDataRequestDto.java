package com.mimaja.job_finder_app.feature.user.manage.userData.shared.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserDataRequestDto(
        @NotBlank String newUsername,
        @NotBlank String newFirstName,
        @NotBlank String newLastName,
        @NotBlank String newProfileDescription,
        @NotBlank String password) {}
