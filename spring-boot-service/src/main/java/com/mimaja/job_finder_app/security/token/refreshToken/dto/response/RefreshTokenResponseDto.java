package com.mimaja.job_finder_app.security.token.refreshToken.dto.response;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenResponseDto(
        @NotBlank String refreshToken, @NotBlank String refreshTokenId) {}
