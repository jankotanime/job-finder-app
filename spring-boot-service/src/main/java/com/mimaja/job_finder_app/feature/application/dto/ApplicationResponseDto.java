package com.mimaja.job_finder_app.feature.application.dto;

import com.mimaja.job_finder_app.feature.application.model.ApplicationStatus;
import com.mimaja.job_finder_app.feature.cv.dto.CvResponseDto;
import com.mimaja.job_finder_app.feature.user.dto.UserInOfferResponseDto;
import java.util.UUID;

public record ApplicationResponseDto(
        UUID id,
        ApplicationStatus status,
        UserInOfferResponseDto candidate,
        CvResponseDto chosenCv) {}
