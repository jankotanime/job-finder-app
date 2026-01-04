package com.mimaja.job_finder_app.feature.application.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.application.dto.ApplicationCreateRequestDto;
import com.mimaja.job_finder_app.feature.application.mapper.ApplicationMapper;
import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.application.model.ApplicationStatus;
import com.mimaja.job_finder_app.feature.application.repository.ApplicationRepository;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.cv.service.CvService;
import com.mimaja.job_finder_app.feature.offer.dto.OfferApplyRequestDto;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.model.OfferStatus;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationServiceDefault implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final OfferService offerService;
    private final UserService userService;
    private final CvService cvService;
    private final ApplicationMapper applicationMapper;

    @Override
    public Page<Application> getAllApplicationsByOfferId(UUID offerId, Pageable pageable) {
        offerService.getOfferById(offerId);
        return applicationRepository.findAllByOfferId(offerId, pageable);
    }

    @Override
    public Application getApplicationById(UUID offerId, UUID applicationId) {
        return getOrThrow(applicationId);
    }

    @Override
    @Transactional
    public Application acceptApplication(UUID offerId, UUID applicationId) {
        offerService.getOfferById(offerId);
        Application application = getOrThrow(applicationId);
        if (!application.getStatus().equals(ApplicationStatus.SENT)) {
            throw new BusinessException(BusinessExceptionReason.APPLICATION_ALREADY_REVIEWED);
        }
        application.setStatus(ApplicationStatus.ACCEPTED);
        return applicationRepository.save(application);
    }

    @Override
    @Transactional
    public Application rejectApplication(UUID offerId, UUID applicationId) {
        offerService.getOfferById(offerId);
        Application application = getOrThrow(applicationId);
        if (!application.getStatus().equals(ApplicationStatus.SENT)) {
            throw new BusinessException(BusinessExceptionReason.APPLICATION_ALREADY_REVIEWED);
        }
        application.setStatus(ApplicationStatus.REJECTED);
        return applicationRepository.save(application);
    }

    @Override
    @Transactional
    public Application sendApplication(
            UUID offerId, UUID userId, OfferApplyRequestDto offerApplyRequestDto) {
        Offer offer = offerService.getOfferById(offerId);
        if (!offer.getStatus().equals(OfferStatus.OPEN)) {
            throw new BusinessException(BusinessExceptionReason.OFFER_CANDIDATES_LIMIT);
        }
        Cv chosenCv = cvService.getCvById(offerApplyRequestDto.cvId());
        User candidate = userService.getUserById(userId);
        Application application =
                applicationMapper.toEntity(
                        new ApplicationCreateRequestDto(candidate, offer, chosenCv));
        offer.addApplication(application);
        return applicationRepository.save(application);
    }

    private Application getOrThrow(UUID applicationId) {
        return applicationRepository
                .findById(applicationId)
                .orElseThrow(
                        () -> new BusinessException(BusinessExceptionReason.APPLICATION_NOT_FOUND));
    }
}
