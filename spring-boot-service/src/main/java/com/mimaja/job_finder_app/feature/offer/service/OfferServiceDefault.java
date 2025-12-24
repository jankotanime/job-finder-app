package com.mimaja.job_finder_app.feature.offer.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.filterspecification.OfferFilterSpecification;
import com.mimaja.job_finder_app.feature.offer.mapper.OfferMapper;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.offerphoto.dto.OfferPhotoCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto;
import com.mimaja.job_finder_app.feature.offer.repository.OfferRepository;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import com.mimaja.job_finder_app.shared.adapters.MultipartFileSource;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.FileFolderName;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class OfferServiceDefault implements OfferService {
    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;
    private final UserService userService;
    private final TagService tagService;
    private final FileManagementService fileManagementService;

    @Override
    public Page<Offer> getAllOffers(Pageable pageable) {
        return offerRepository.findAll(pageable);
    }

    @Override
    public Offer getOfferById(UUID offerId) {
        return getOrThrow(offerId);
    }

    @Override
    @Transactional
    public Offer createOffer(
            Optional<MultipartFile[]> photos,
            OfferCreateRequestDto offerCreateRequestDto,
            UUID userId) {
        User owner = userService.getUserById(userId);
        Set<Tag> tags =
                offerCreateRequestDto.tags().stream()
                        .map(tagService::getTagById)
                        .collect(Collectors.toSet());

        Offer offer = offerMapper.toEntity(offerCreateRequestDto);
        offer.setOwner(owner);
        offer.setTags(tags);
        if (photos.isPresent()) {
            Set<OfferPhoto> offerPhotos =
                    photos.get().length > 0
                            ? Arrays.stream(photos.get())
                                    .map(this::processPhoto)
                                    .collect(Collectors.toSet())
                            : null;
            offer.setPhotos(offerPhotos);
        }
        return offerRepository.save(offer);
    }

    @Override
    @Transactional
    public Offer updateOffer(
            UUID offerId,
            Optional<MultipartFile[]> photos,
            OfferUpdateRequestDto offerUpdateRequestDto) {
        Offer offer = getOrThrow(offerId);
        Set<Tag> tags =
                offerUpdateRequestDto.tags().stream()
                        .map(tagService::getTagById)
                        .collect(Collectors.toSet());
        offer.getPhotos().forEach(photo -> fileManagementService.deleteFile(photo.getStorageKey()));
        Set<OfferPhoto> offerPhotos =
                photos.map(
                                multipartFiles ->
                                        Arrays.stream(multipartFiles)
                                                .map(this::processPhoto)
                                                .collect(Collectors.toSet()))
                        .orElse(new HashSet<>());

        offer.update(offerMapper.toEntityFromUpdate(offerUpdateRequestDto), tags, offerPhotos);
        return offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void deleteOffer(UUID offerId) {
        Offer offer = getOrThrow(offerId);
        offerRepository.delete(offer);
    }

    @Override
    public Page<Offer> getFilteredOffers(OfferFilterRequestDto dto, Pageable pageable) {
        return offerRepository.findAll(OfferFilterSpecification.filter(dto), pageable);
    }

    private Offer getOrThrow(UUID offerId) {
        Optional<Offer> offerOpt = offerRepository.findById(offerId);
        if (offerOpt.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.OFFER_NOT_FOUND);
        }

        return offerOpt.get();
    }

    private OfferPhoto processPhoto(MultipartFile photo) {
        MultipartFileSource fileSource = new MultipartFileSource(photo);
        String folder = FileFolderName.PHOTOS.getFullPath(FileFolderName.OFFER_PHOTO);
        ProcessedFileDetails fileDetails =
                fileManagementService.processFileDetails(fileSource, folder);

        fileManagementService.uploadFile(fileDetails);

        OfferPhotoCreateRequestDto dto = OfferPhotoCreateRequestDto.from(fileDetails);
        return OfferPhoto.from(dto);
    }
}
