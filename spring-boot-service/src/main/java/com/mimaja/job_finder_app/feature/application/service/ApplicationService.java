package com.mimaja.job_finder_app.feature.application.service;

import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.offer.dto.OfferApplyRequestDto;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {
    Page<Application> getAllApplicationsByOfferId(UUID offerId, Pageable pageable);

    Application getApplicationById(UUID offerId, UUID applicationId);

    Application sendApplication(UUID offerId, UUID userId, OfferApplyRequestDto dto);

    Application acceptApplication(UUID offerId, UUID applicationId);

    Application rejectApplication(UUID offerId, UUID applicationId);
}
