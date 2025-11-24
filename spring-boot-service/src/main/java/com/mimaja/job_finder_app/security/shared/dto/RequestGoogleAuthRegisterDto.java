package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotNull;

public record RequestGoogleAuthRegisterDto(
        @NotNull String googleToken, @NotNull String username, @NotNull int phoneNumber) {}
