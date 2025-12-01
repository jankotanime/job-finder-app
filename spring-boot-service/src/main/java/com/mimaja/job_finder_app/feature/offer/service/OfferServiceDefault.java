package com.mimaja.job_finder_app.feature.offer.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.dto.OfferBaseResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferSummaryResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUserIsOwnerResponseDto;
import com.mimaja.job_finder_app.feature.offer.mapper.OfferMapper;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.repository.OfferRepository;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferServiceDefault implements OfferService {
    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;
    private final UserService userService;
    private final TagService tagService;

    @Override
    public Page<OfferSummaryResponseDto> getAllOffers(Pageable pageable) {
        return offerRepository.findAll(pageable).map(offerMapper::toOfferSummaryResponseDto);
    }

    @Override
    public OfferBaseResponseDto getOfferById(Jwt jwt, UUID offerId) {
        Offer offer = getOrThrow(offerId);
        UUID userId = UUID.fromString(jwt.getClaims().get("userId").toString());
        if (checkIfUserIsOwner(userId, offerId)) {
            return offerMapper.toOfferUserIsOwnerResponseDto(offer);
        }
        return offerMapper.toOfferSummaryResponseDto(offer);
    }

    @Override
    public OfferUserIsOwnerResponseDto createOffer(OfferCreateRequestDto offerCreateRequestDto) {
        User user = userService.getUserById(offerCreateRequestDto.ownerId());
        Set<Tag> tags =
                offerCreateRequestDto.tags().stream()
                        .map(tagService::getTagById)
                        .collect(Collectors.toSet());

        Offer offer = offerRepository.save(offerMapper.toEntity(offerCreateRequestDto, user, tags));
        return offerMapper.toOfferUserIsOwnerResponseDto(offer);
    }

    @Override
    public OfferUserIsOwnerResponseDto updateOffer(
            UUID offerId, OfferUpdateRequestDto offerUpdateRequestDto) {
        Offer offer = getOrThrow(offerId);
        Set<Tag> tags =
                offerUpdateRequestDto.tags().stream()
                        .map(tagService::getTagById)
                        .collect(Collectors.toSet());

        offer.update(offerMapper.toEntity(offerUpdateRequestDto, tags));
        Offer updatedOffer = offerRepository.save(offer);

        return offerMapper.toOfferUserIsOwnerResponseDto(updatedOffer);
    }

    @Override
    public void deleteOffer(UUID offerId) {
        Offer offer = getOrThrow(offerId);
        offerRepository.delete(offer);
    }

    @Override
    public boolean checkIfUserIsOwner(UUID userId, UUID offerId) {
        Offer offer = getOrThrow(offerId);
        return (offer.getOwner().getId()).equals(userId);
    }

    private Offer getOrThrow(UUID offerId) {
        Optional<Offer> offerOpt = offerRepository.findById(offerId);
        if (offerOpt.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.OFFER_NOT_FOUND);
        }

        return offerOpt.get();
    }
}
