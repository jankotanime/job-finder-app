package com.mimaja.job_finder_app.security.shared.dto;

public record ResponseRefreshTokenDto(
  String refreshToken,
  String refreshTokenId
) {}
