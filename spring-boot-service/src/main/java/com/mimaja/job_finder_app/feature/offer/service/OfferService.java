package com.mimaja.job_finder_app.feature.offer.service;

import com.mimaja.job_finder_app.feature.offer.dto.OfferBaseResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferSummaryResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUserIsOwnerResponseDto;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OfferService {
    Page<OfferSummaryResponseDto> getAllOffers(Pageable pageable);

    OfferBaseResponseDto getOfferById(JwtPrincipal jwt, UUID offerId);

    OfferUserIsOwnerResponseDto createOffer(OfferCreateRequestDto offerCreateRequestDto);

    OfferUserIsOwnerResponseDto updateOffer(
            UUID offerId, OfferUpdateRequestDto offerUpdateRequestDto);

    void deleteOffer(UUID offerId);

    boolean checkIfUserIsOwner(UUID userId, UUID offerId);
}
