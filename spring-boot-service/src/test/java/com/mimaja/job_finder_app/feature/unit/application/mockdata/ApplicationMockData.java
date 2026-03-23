package com.mimaja.job_finder_app.feature.unit.application.mockdata;

import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.application.model.ApplicationStatus;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.user.model.User;
import java.util.UUID;

public class ApplicationMockData {

    public static Application createTestApplication(User candidate, Offer offer, Cv cv) {
        Application application = new Application();
        application.setId(UUID.randomUUID());
        application.setCandidate(candidate);
        application.setOffer(offer);
        application.setStatus(ApplicationStatus.SENT);
        return application;
    }

    public static Application createTestApplicationWithStatus(
            User candidate, Offer offer, Cv cv, ApplicationStatus status) {
        Application application = createTestApplication(candidate, offer, cv);
        application.setStatus(status);
        return application;
    }
}

