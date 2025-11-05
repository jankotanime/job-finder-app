package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotNull;

public record ResponseRefreshTokenDto(
        @NotNull String refreshToken, @NotNull String refreshTokenId) {}
