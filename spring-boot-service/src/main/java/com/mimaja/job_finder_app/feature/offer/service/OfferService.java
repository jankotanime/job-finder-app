package com.mimaja.job_finder_app.feature.offer.service;

import com.mimaja.job_finder_app.feature.offer.dto.OfferApplyRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface OfferService {
    Offer getOfferById(UUID offerId);

    Offer createOffer(
            Optional<MultipartFile> photo,
            OfferCreateRequestDto offerCreateRequestDto,
            UUID userId);

    Offer updateOffer(
            UUID offerId,
            Optional<MultipartFile> photo,
            OfferUpdateRequestDto offerUpdateRequestDto);

    void deleteOffer(UUID offerId);

    void deleteOffersByOwnerId(UUID userId);

    Page<Offer> getFilteredOffers(OfferFilterRequestDto offerFilterRequestDto, Pageable pageable);

    Offer applyOffer(UUID offerId, UUID userId, OfferApplyRequestDto dto);
}
