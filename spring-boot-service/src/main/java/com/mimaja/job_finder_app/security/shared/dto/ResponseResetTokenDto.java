package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotBlank;

public record ResponseResetTokenDto(@NotBlank String resetToken, @NotBlank String resetTokenId) {}
