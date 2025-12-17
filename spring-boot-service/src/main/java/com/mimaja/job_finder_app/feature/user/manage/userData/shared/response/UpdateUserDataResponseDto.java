package com.mimaja.job_finder_app.feature.user.manage.userData.shared.response;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserDataResponseDto(@NotBlank String accessToken) {}
