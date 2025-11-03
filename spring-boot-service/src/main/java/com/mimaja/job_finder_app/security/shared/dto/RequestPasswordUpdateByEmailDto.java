package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotNull;

public record RequestPasswordUpdateByEmailDto(
  @NotNull
  String password,
  @NotNull
  String token,
  @NotNull
  String tokenId) {}
