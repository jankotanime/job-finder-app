package com.mimaja.job_finder_app.security.shared.dto;

import com.mimaja.job_finder_app.security.shared.enums.GoogleIdExistence;
import jakarta.validation.constraints.NotNull;

public record ResponseGoogleIdExistDto(@NotNull GoogleIdExistence exist) {}
