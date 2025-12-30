package com.mimaja.job_finder_app.feature.user.dto;

import com.mimaja.job_finder_app.feature.user.profilephoto.dto.ProfilePhotoResponseDto;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserAdminPanelResponseDto(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        int phoneNumber,
        String profileDescription,
        ProfilePhotoResponseDto profilePhoto,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
