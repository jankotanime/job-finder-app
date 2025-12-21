package com.mimaja.job_finder_app.feature.offer.service;

import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferSummaryResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUserIsOwnerResponseDto;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OfferService {
    Page<OfferSummaryResponseDto> getAllOffers(Pageable pageable);

    Offer getOfferById(UUID offerId);

    OfferUserIsOwnerResponseDto createOffer(OfferCreateRequestDto offerCreateRequestDto);

    OfferUserIsOwnerResponseDto updateOffer(
            UUID offerId, OfferUpdateRequestDto offerUpdateRequestDto);

    void deleteOffer(UUID offerId);

    Page<OfferSummaryResponseDto> getFilteredOffers(
            OfferFilterRequestDto offerFilterRequestDto, Pageable pageable);
}
