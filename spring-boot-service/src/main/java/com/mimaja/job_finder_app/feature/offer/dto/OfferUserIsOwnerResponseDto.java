package com.mimaja.job_finder_app.feature.offer.dto;

import com.mimaja.job_finder_app.feature.application.dto.ApplicationResponseDto;
import com.mimaja.job_finder_app.feature.offer.model.OfferStatus;
import com.mimaja.job_finder_app.feature.offer.offerphoto.dto.OfferPhotoResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.user.dto.UserInOfferResponseDto;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record OfferUserIsOwnerResponseDto(
        UUID id,
        String title,
        String description,
        LocalDateTime dateAndTime,
        Double salary,
        OfferStatus status,
        int maxApplications,
        Set<TagResponseDto> tags,
        Set<ApplicationResponseDto> applications,
        @Nullable UserInOfferResponseDto chosenCandidate,
        @Nullable OfferPhotoResponseDto photo)
        implements OfferBaseResponseDto {}
