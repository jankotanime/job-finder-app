package com.mimaja.job_finder_app.security.shared.dto;

import jakarta.validation.constraints.NotNull;

public record ResponseResetTokenDto(@NotNull String resetToken, @NotNull String resetTokenId) {}
