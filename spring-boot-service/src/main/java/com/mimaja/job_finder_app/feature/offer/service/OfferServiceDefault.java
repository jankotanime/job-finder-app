package com.mimaja.job_finder_app.feature.offer.service;

import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationExceptionReason;
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
import com.mimaja.job_finder_app.shared.enums.MimeType;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class OfferServiceDefault implements OfferService {
    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;
    private final UserService userService;
    private final TagService tagService;
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessedPhotoDetails {
        String fileName;
        String contentType;
        MimeType mimeType;
        String storageKey;
    }

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
        offer.getPhotos()
                .forEach(
                        photo ->
                                s3Client.deleteObject(
                                        DeleteObjectRequest.builder()
                                                .bucket(bucket)
                                                .key(photo.getStorageKey())
                                                .build()));
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
        ProcessedPhotoDetails fileDetails = processFileDetails(photo);

        PutObjectRequest req =
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileDetails.storageKey)
                        .contentType(fileDetails.contentType)
                        .build();

        try {
            s3Client.putObject(req, RequestBody.fromBytes(photo.getBytes()));
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionReason.FILE_UPLOAD_EXCEPTION);
        }

        BigInteger fileSize = BigInteger.valueOf(photo.getSize());

        OfferPhotoCreateRequestDto dto =
                new OfferPhotoCreateRequestDto(
                        fileDetails.getFileName(),
                        fileDetails.getMimeType(),
                        fileSize,
                        fileDetails.getStorageKey());
        return OfferPhoto.from(dto);
    }

    private MimeType getFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            throw new IllegalArgumentException("Invalid file extension in filename: " + filename);
        }
        return MimeType.valueOf(filename.substring(idx + 1).toUpperCase());
    }

    private String getFileName(MultipartFile file) {
        return Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(
                        () ->
                                new ApplicationException(
                                        ApplicationExceptionReason.FILE_NAME_MISSING))
                .toLowerCase();
    }

    private String getContentType(MultipartFile file) {
        return Optional.ofNullable(file.getContentType())
                .orElseThrow(
                        () ->
                                new ApplicationException(
                                        ApplicationExceptionReason.CONTENT_TYPE_UNKNOWN));
    }

    private ProcessedPhotoDetails processFileDetails(MultipartFile file) {
        String fileName = getFileName(file);
        String contentType = getContentType(file);
        MimeType ext = getFileExtension(fileName);
        if (!ext.equals(MimeType.JPG) && !ext.equals(MimeType.JPEG) && !ext.equals(MimeType.PNG)) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PHOTO_FORMAT);
        }
        String folder = "photos/offer-photos";

        String key = String.format("%s/%s-%s", folder, UUID.randomUUID(), fileName);
        return new ProcessedPhotoDetails(fileName, contentType, ext, key);
    }
}
