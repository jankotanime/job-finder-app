package com.mimaja.job_finder_app.feature.offer.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferSummaryResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUserIsOwnerResponseDto;
import com.mimaja.job_finder_app.feature.offer.filterspecification.OfferFilterSpecification;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Offer getOfferById(UUID offerId) {
        return getOrThrow(offerId);
    }

    @Override
    @Transactional
    public OfferUserIsOwnerResponseDto createOffer(OfferCreateRequestDto offerCreateRequestDto) {
        User owner = userService.getUserById(offerCreateRequestDto.ownerId());
        Set<Tag> tags =
                offerCreateRequestDto.tags().stream()
                        .map(tagService::getTagById)
                        .collect(Collectors.toSet());

        Offer offer = offerMapper.toEntity(offerCreateRequestDto);
        offer.setOwner(owner);
        offer.setTags(tags);
        offerRepository.save(offer);

        return offerMapper.toOfferUserIsOwnerResponseDto(offer);
    }

    @Override
    @Transactional
    public OfferUserIsOwnerResponseDto updateOffer(
            UUID offerId, OfferUpdateRequestDto offerUpdateRequestDto) {
        Offer offer = getOrThrow(offerId);
        Set<Tag> tags =
                offerUpdateRequestDto.tags().stream()
                        .map(tagService::getTagById)
                        .collect(Collectors.toSet());

        offer.update(offerMapper.toEntityFromUpdate(offerUpdateRequestDto), tags);
        Offer updatedOffer = offerRepository.save(offer);

        return offerMapper.toOfferUserIsOwnerResponseDto(updatedOffer);
    }

    @Override
    @Transactional
    public void deleteOffer(UUID offerId) {
        Offer offer = getOrThrow(offerId);
        offerRepository.delete(offer);
    }

    @Override
    public Page<OfferSummaryResponseDto> getFilteredOffers(
            OfferFilterRequestDto dto, Pageable pageable) {
        Page<Offer> offers =
                offerRepository.findAll(OfferFilterSpecification.filter(dto), pageable);
        return offers.map(offerMapper::toOfferSummaryResponseDto);
    }

    private Offer getOrThrow(UUID offerId) {
        Optional<Offer> offerOpt = offerRepository.findById(offerId);
        if (offerOpt.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.OFFER_NOT_FOUND);
        }

        return offerOpt.get();
    }
}
