package com.mimaja.job_finder_app.feature.offer.dto;

import com.mimaja.job_finder_app.feature.contract.dto.response.ContractDto;
import com.mimaja.job_finder_app.feature.offer.model.OfferStatus;
import com.mimaja.job_finder_app.feature.offer.offerphoto.dto.OfferPhotoResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.user.dto.UserInOfferResponseDto;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record OfferSummaryResponseDto(
        UUID id,
        String title,
        String description,
        LocalDateTime dateAndTime,
        Double salary,
        OfferStatus status,
        int maxApplications,
        UserInOfferResponseDto owner,
        Set<TagResponseDto> tags,
        int candidatesAmount,
        @Nullable OfferPhotoResponseDto photo,
        @Nullable ContractDto contract)
        implements OfferBaseResponseDto {}
