package com.mimaja.job_finder_app.feature.application.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.application.dto.ApplicationResponseDto;
import com.mimaja.job_finder_app.feature.application.mapper.ApplicationMapper;
import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.offer.dto.OfferApplyRequestDto;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationServiceUser {
    private final ApplicationService applicationService;
    private final ApplicationMapper applicationMapper;
    private final OfferService offerService;

    public Page<ApplicationResponseDto> getAllApplicationsByOfferId(
            UUID offerId, JwtPrincipal jwt, Pageable pageable) {
        throwErrorIfNotOfferOwner(offerId, jwt);
        return applicationService
                .getAllApplicationsByOfferId(offerId, pageable)
                .map(applicationMapper::toResponseDto);
    }

    public ApplicationResponseDto getApplicationById(
            UUID offerId, UUID applicationId, JwtPrincipal jwt) {
        throwErrorIfNotOfferOwnerOrApplicationOwner(offerId, applicationId, jwt);
        return applicationMapper.toResponseDto(
                applicationService.getApplicationById(offerId, applicationId));
    }

    public ApplicationResponseDto sendApplication(
            UUID offerId, JwtPrincipal jwt, OfferApplyRequestDto dto) {
        validateCandidate(jwt.id(), offerId);
        return applicationMapper.toResponseDto(
                applicationService.sendApplication(offerId, jwt.id(), dto));
    }

    public ApplicationResponseDto acceptApplication(
            UUID offerId, UUID applicationId, JwtPrincipal jwt) {
        throwErrorIfNotOfferOwner(offerId, jwt);
        return applicationMapper.toResponseDto(
                applicationService.acceptApplication(offerId, applicationId));
    }

    public ApplicationResponseDto rejectApplication(
            UUID offerId, UUID applicationId, JwtPrincipal jwt) {
        throwErrorIfNotOfferOwner(offerId, jwt);
        return applicationMapper.toResponseDto(
                applicationService.rejectApplication(offerId, applicationId));
    }

    private void throwErrorIfNotOfferOwner(UUID offerId, JwtPrincipal jwt) {
        Offer offer = offerService.getOfferById(offerId);
        if (!offer.getOwner().getId().equals(jwt.id())) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_OWNER);
        }
    }

    private void throwErrorIfNotOfferOwnerOrApplicationOwner(
            UUID offerId, UUID applicationId, JwtPrincipal jwt) {
        Application application = applicationService.getApplicationById(offerId, applicationId);
        if (!application.getOffer().getOwner().getId().equals(jwt.id())
                && !application.getCandidate().getId().equals(jwt.id())) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_OWNER);
        }
    }

    private boolean checkIfUserIsOwner(UUID userId, Offer offer) {
        return (offer.getOwner().getId()).equals(userId);
    }

    private void validateCandidate(UUID userId, UUID offerId) {
        Offer offer = offerService.getOfferById(offerId);
        for (Application application : offer.getApplications()) {
            if (application.getCandidate().getId().equals(userId)) {
                throw new BusinessException(BusinessExceptionReason.ALREADY_APPLIED_FOR_OFFER);
            }
        }
        if (checkIfUserIsOwner(userId, offer)) {
            throw new BusinessException(BusinessExceptionReason.OWNER_CANNOT_APPLY);
        }
    }
}
