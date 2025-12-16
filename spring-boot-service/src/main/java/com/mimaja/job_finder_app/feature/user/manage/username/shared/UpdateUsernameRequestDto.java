package com.mimaja.job_finder_app.feature.user.manage.username.shared;

import jakarta.validation.constraints.NotBlank;

public record UpdateUsernameRequestDto(@NotBlank String newUsername, @NotBlank String password) {}
