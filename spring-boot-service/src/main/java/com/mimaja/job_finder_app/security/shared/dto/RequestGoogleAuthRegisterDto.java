package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestGoogleAuthRegisterDto(
        @NotBlank String googleToken, @NotBlank String username, @NotNull int phoneNumber) {}
