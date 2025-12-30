package com.mimaja.job_finder_app.feature.user.service;

import com.mimaja.job_finder_app.feature.cv.service.CvService;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDeletionService {
    private final CvService cvService;
    private final OfferService offerService;
    private final UserService userService;

    @Transactional
    public void deleteUser(UUID userId) {
        cvService.deleteAllCvsForUser(userId);
        offerService.deleteOffersByOwnerId(userId);
        userService.deleteUser(userId);
    }
}
