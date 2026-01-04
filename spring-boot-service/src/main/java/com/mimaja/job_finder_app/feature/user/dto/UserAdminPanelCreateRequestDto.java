package com.mimaja.job_finder_app.feature.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserAdminPanelCreateRequestDto(
        @NotBlank String username,
        @NotBlank String email,
        @NotNull int phoneNumber,
        @NotBlank String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String profileDescription) {}
