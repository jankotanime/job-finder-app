package com.mimaja.job_finder_app.feature.offer.dto;

import com.mimaja.job_finder_app.feature.offer.location.dto.LocationResponseDto;
import com.mimaja.job_finder_app.feature.offer.model.OfferStatus;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.user.dto.UserInOfferResponseDto;
import com.mimaja.job_finder_app.shared.dto.PhotoDto;
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
        LocationResponseDto location,
        OfferStatus status,
        int maxParticipants,
        UserInOfferResponseDto owner,
        Set<TagResponseDto> tags,
        Set<UserInOfferResponseDto> candidates,
        @Nullable UserInOfferResponseDto chosenCandidate,
        @Nullable PhotoDto photo)
        implements OfferBaseResponseDto {}
