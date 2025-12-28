package com.mimaja.job_finder_app.feature.offer.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.application.dto.ApplicationCreateRequestDto;
import com.mimaja.job_finder_app.feature.application.mapper.ApplicationMapper;
import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.cv.service.CvService;
import com.mimaja.job_finder_app.feature.offer.dto.OfferApplyRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.filterspecification.OfferFilterSpecification;
import com.mimaja.job_finder_app.feature.offer.mapper.OfferMapper;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.model.OfferStatus;
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
    private final CvService cvService;
    private final ApplicationMapper applicationMapper;
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
            Optional<MultipartFile> photo,
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
        OfferPhoto offerPhoto = null;
        if (photo.isPresent()) {
            offerPhoto = processPhoto(photo.get());
        }
        offer.setPhoto(offerPhoto);
        return offerRepository.save(offer);
    }

    @Override
    @Transactional
    public Offer updateOffer(
            UUID offerId,
            Optional<MultipartFile> photo,
            OfferUpdateRequestDto offerUpdateRequestDto) {
        Offer offer = getOrThrow(offerId);
        Set<Tag> tags =
                offerUpdateRequestDto.tags().stream()
                        .map(tagService::getTagById)
                        .collect(Collectors.toSet());
        if (offer.getPhoto() != null) {
            fileManagementService.deleteFile(offer.getPhoto().getStorageKey());
        }

        OfferPhoto offerPhoto = null;
        if (photo.isPresent()) {
            offerPhoto = processPhoto(photo.get());
        }
        offer.update(offerMapper.toEntityFromUpdate(offerUpdateRequestDto), tags, offerPhoto);
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

    @Override
    @Transactional
    public Offer applyOffer(UUID offerId, UUID userId, OfferApplyRequestDto dto) {
        Offer offer = getOrThrow(offerId);
        if (!offer.getStatus().equals(OfferStatus.OPEN)) {
            throw new BusinessException(BusinessExceptionReason.OFFER_CANDIDATES_LIMIT);
        }
        Cv chosenCv = cvService.getCvById(dto.cvId());
        User candidate = userService.getUserById(userId);
        Application application =
                applicationMapper.toEntity(
                        new ApplicationCreateRequestDto(candidate, offer, chosenCv));
        offer.getApplications().add(application);
        return offerRepository.save(offer);
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
