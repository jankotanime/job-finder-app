package com.mimaja.job_finder_app.feature.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserAdminPanelUpdateRequestDto(
        @NotBlank String username,
        @NotBlank String email,
        @NotNull int phoneNumber,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String profileDescription) {}
