package com.mimaja.job_finder_app.security.shared.dto;

public record ResponseTokensDto(
  String accessToken,
  String refreshToken,
  String refreshTokenId
) {}