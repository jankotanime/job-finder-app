package com.mimaja.job_finder_app.feature.user.dto;

import java.util.UUID;

public record UserInOfferResponseDto(
        UUID id, String username, String firstName, String lastName, int phoneNumber) {}
