package com.mimaja.job_finder_app.feature.unit.user.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.cv.service.CvService;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.user.service.UserDeletionService;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDeletionServiceTest {
    @Mock private CvService cvService;
    @Mock private OfferService offerService;
    @Mock private UserService userService;

    private UserDeletionService userDeletionService;

    @BeforeEach
    void setUp() {
        userDeletionService = new UserDeletionService(cvService, offerService, userService);
    }

    @Test
    void deleteUser_shouldCallDeleteAllCvsForUser_whenDeletingUser() {
        UUID userId = UUID.randomUUID();
        userDeletionService.deleteUser(userId);
        verify(cvService, times(1)).deleteAllCvsForUser(userId);
    }

    @Test
    void deleteUser_shouldCallDeleteOffersByOwnerId_whenDeletingUser() {
        UUID userId = UUID.randomUUID();
        userDeletionService.deleteUser(userId);
        verify(offerService, times(1)).deleteOffersByOwnerId(userId);
    }

    @Test
    void deleteUser_shouldCallDeleteUser_whenDeletingUser() {
        UUID userId = UUID.randomUUID();
        userDeletionService.deleteUser(userId);
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void deleteUser_shouldThrowBusinessException_whenCvDeletionFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.CV_NOT_FOUND))
                .when(cvService)
                .deleteAllCvsForUser(userId);
        assertThrows(BusinessException.class, () -> userDeletionService.deleteUser(userId));
    }

    @Test
    void deleteUser_shouldNotCallOfferService_whenCvDeletionFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.CV_NOT_FOUND))
                .when(cvService)
                .deleteAllCvsForUser(userId);
        assertThrows(BusinessException.class, () -> userDeletionService.deleteUser(userId));
        verify(offerService, never()).deleteOffersByOwnerId(any());
    }

    @Test
    void deleteUser_shouldNotCallUserService_whenCvDeletionFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.CV_NOT_FOUND))
                .when(cvService)
                .deleteAllCvsForUser(userId);
        assertThrows(BusinessException.class, () -> userDeletionService.deleteUser(userId));
        verify(userService, never()).deleteUser(any());
    }

    @Test
    void deleteUser_shouldThrowBusinessException_whenOfferDeletionFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.OFFER_NOT_FOUND))
                .when(offerService)
                .deleteOffersByOwnerId(userId);
        assertThrows(BusinessException.class, () -> userDeletionService.deleteUser(userId));
    }

    @Test
    void deleteUser_shouldCallDeleteCvsBeforeFailing_whenOfferDeletionFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.OFFER_NOT_FOUND))
                .when(offerService)
                .deleteOffersByOwnerId(userId);
        assertThrows(BusinessException.class, () -> userDeletionService.deleteUser(userId));
        verify(cvService, times(1)).deleteAllCvsForUser(userId);
    }

    @Test
    void deleteUser_shouldNotCallUserService_whenOfferDeletionFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.OFFER_NOT_FOUND))
                .when(offerService)
                .deleteOffersByOwnerId(userId);
        assertThrows(BusinessException.class, () -> userDeletionService.deleteUser(userId));
        verify(userService, never()).deleteUser(any());
    }

    @Test
    void deleteUser_shouldThrowBusinessException_whenUserDeletionFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userService)
                .deleteUser(userId);
        assertThrows(BusinessException.class, () -> userDeletionService.deleteUser(userId));
    }

    @Test
    void deleteUser_shouldCallDeleteCvs_whenUserDeletionFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userService)
                .deleteUser(userId);
        assertThrows(BusinessException.class, () -> userDeletionService.deleteUser(userId));
        verify(cvService, times(1)).deleteAllCvsForUser(userId);
    }

    @Test
    void deleteUser_shouldCallDeleteOffers_whenUserDeletionFails() {
        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
                .when(userService)
                .deleteUser(userId);
        assertThrows(BusinessException.class, () -> userDeletionService.deleteUser(userId));
        verify(offerService, times(1)).deleteOffersByOwnerId(userId);
    }
}
