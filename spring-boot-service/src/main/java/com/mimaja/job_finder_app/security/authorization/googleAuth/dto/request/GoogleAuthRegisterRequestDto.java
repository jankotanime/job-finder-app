package com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GoogleAuthRegisterRequestDto(
        @NotBlank String googleToken, @NotBlank String username, @NotNull int phoneNumber) {}
