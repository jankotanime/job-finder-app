package com.mimaja.job_finder_app.feature.user.manage.phoneNumber.shared.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePhoneNumberRequestDto(@NotNull int newPhoneNumber, @NotBlank String password) {}
