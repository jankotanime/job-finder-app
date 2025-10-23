package com.mimaja.job_finder_app.security.shared.dto;

public record RequestRefreshTokenRotateDto(
  String refreshToken,
  String refreshTokenId
) {}
