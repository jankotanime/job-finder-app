package com.mimaja.job_finder_app.feature.offer.service;

import com.mimaja.job_finder_app.feature.offer.dto.OfferBaseResponseDto;
import com.mimaja.job_finder_app.feature.offer.mapper.OfferMapper;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferUserService {
    private final OfferService offerService;
    private final OfferMapper offerMapper;

    public OfferBaseResponseDto getOfferById(JwtPrincipal jwt, UUID offerId) {
        Offer offer = offerService.getOfferById(offerId);
        UUID userId = jwt.id();
        if (checkIfUserIsOwner(userId, offer)) {
            return offerMapper.toOfferUserIsOwnerResponseDto(offer);
        }
        return offerMapper.toOfferSummaryResponseDto(offerService.getOfferById(offerId));
    }

    private boolean checkIfUserIsOwner(UUID userId, Offer offer) {
        return (offer.getOwner().getId()).equals(userId);
    }
}
