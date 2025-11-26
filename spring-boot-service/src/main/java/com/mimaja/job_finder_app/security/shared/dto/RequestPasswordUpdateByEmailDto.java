package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotBlank;

public record RequestPasswordUpdateByEmailDto(
        @NotBlank String password, @NotBlank String token, @NotBlank String tokenId) {}
