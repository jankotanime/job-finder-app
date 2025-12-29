package com.mimaja.job_finder_app.security.token.refreshToken.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RequestRefreshTokenRotateDto(
        @NotBlank String refreshToken, @NotBlank String refreshTokenId) {}
