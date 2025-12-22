package com.mimaja.job_finder_app.security.token.resetToken.dto.response;

import jakarta.validation.constraints.NotBlank;

public record ResetTokenResponseDto(@NotBlank String resetToken, @NotBlank String resetTokenId) {}
