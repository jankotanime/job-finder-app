package com.mimaja.job_finder_app.feature.user.dto;

import com.mimaja.job_finder_app.feature.user.profilephoto.model.ProfilePhoto;
import jakarta.annotation.Nullable;
import java.util.UUID;

public record UserInOfferResponseDto(
        UUID id,
        String username,
        String firstName,
        String lastName,
        int phoneNumber,
        @Nullable ProfilePhoto profilePhoto) {}
