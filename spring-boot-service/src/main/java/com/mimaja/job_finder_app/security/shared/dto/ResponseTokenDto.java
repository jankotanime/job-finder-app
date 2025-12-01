package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotBlank;

public record ResponseTokenDto(
        @NotBlank String accessToken,
        @NotBlank String refreshToken,
        @NotBlank String refreshTokenId) {}
