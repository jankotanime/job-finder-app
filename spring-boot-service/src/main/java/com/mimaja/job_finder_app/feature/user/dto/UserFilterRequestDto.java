package com.mimaja.job_finder_app.feature.user.dto;

import java.time.LocalDateTime;

public record UserFilterRequestDto(
        String username, String email, LocalDateTime firstDate, LocalDateTime lastDate) {}
