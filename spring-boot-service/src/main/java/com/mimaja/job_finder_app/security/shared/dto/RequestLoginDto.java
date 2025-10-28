package com.mimaja.job_finder_app.security.shared.dto;

public record RequestLoginDto(
  String loginData,
  String password
) {}
