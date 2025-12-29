package com.mimaja.job_finder_app.feature.user.update.shared.requestDto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordByEmailRequestDto(
        @NotBlank String password, @NotBlank String token, @NotBlank String tokenId) {}
