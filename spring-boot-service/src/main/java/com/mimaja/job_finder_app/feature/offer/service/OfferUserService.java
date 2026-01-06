package com.mimaja.job_finder_app.feature.offer.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.dto.OfferBaseResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferSummaryResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUserIsOwnerResponseDto;
import com.mimaja.job_finder_app.feature.offer.mapper.OfferMapper;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class OfferUserService {
    private final OfferService offerService;
    private final OfferMapper offerMapper;

    public OfferBaseResponseDto getOfferById(JwtPrincipal jwt, UUID offerId) {
        Offer offer = offerService.getOfferById(offerId);
        if (checkIfUserIsOwner(jwt.id(), offer)) {
            return offerMapper.toOfferUserIsOwnerResponseDto(offer);
        }
        return offerMapper.toOfferSummaryResponseDto(offerService.getOfferById(offerId));
    }

    public OfferUserIsOwnerResponseDto createOffer(
            Optional<MultipartFile> photo,
            OfferCreateRequestDto offerCreateRequestDto,
            JwtPrincipal jwt) {
        return offerMapper.toOfferUserIsOwnerResponseDto(
                offerService.createOffer(photo, offerCreateRequestDto, jwt.id()));
    }

    public OfferUserIsOwnerResponseDto updateOffer(
            UUID offerId,
            Optional<MultipartFile> photo,
            OfferUpdateRequestDto offerUpdateRequestDto,
            JwtPrincipal jwt) {
        throwErrorIfUserIsNotOwner(jwt.id(), offerId);
        return offerMapper.toOfferUserIsOwnerResponseDto(
                offerService.updateOffer(offerId, photo, offerUpdateRequestDto));
    }

    public void deleteOffer(UUID offerId, JwtPrincipal jwt) {
        throwErrorIfUserIsNotOwner(jwt.id(), offerId);
        offerService.deleteOffer(offerId);
    }

    public Page<OfferSummaryResponseDto> getFilteredOffers(
            OfferFilterRequestDto offerFilterRequestDto, Pageable pageable) {
        return offerService
                .getFilteredOffers(offerFilterRequestDto, pageable)
                .map(offerMapper::toOfferSummaryResponseDto);
    }

    private boolean checkIfUserIsOwner(UUID userId, Offer offer) {
        return (offer.getOwner().getId()).equals(userId);
    }

    private void throwErrorIfUserIsNotOwner(UUID userId, UUID offerId) {
        Offer offer = offerService.getOfferById(offerId);
        if (!offer.getOwner().getId().equals(userId)) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_OWNER);
        }
    }
}
