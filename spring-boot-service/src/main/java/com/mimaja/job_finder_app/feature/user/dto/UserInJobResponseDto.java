package com.mimaja.job_finder_app.feature.user.dto;

import com.mimaja.job_finder_app.feature.user.profilephoto.dto.ProfilePhotoResponseDto;
import java.util.UUID;

public record UserInJobResponseDto(
        UUID id,
        String username,
        String firstName,
        String lastName,
        int phoneNumber,
        ProfilePhotoResponseDto profilePhoto) {}
