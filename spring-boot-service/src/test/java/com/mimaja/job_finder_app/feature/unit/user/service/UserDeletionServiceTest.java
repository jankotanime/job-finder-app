package com.mimaja.job_finder_app.feature.unit.user.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.cv.service.CvService;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.user.service.UserDeletionService;
import com.mimaja.job_finder_app.feature.user.service.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDeletionService - Unit Tests")
public class UserDeletionServiceTest {

    @Mock
    private CvService cvService;

    @Mock
    private OfferService offerService;

    @Mock
    private UserService userService;

    private UserDeletionService userDeletionService;

    void setUp() {
        userDeletionService = new UserDeletionService(cvService, offerService, userService);
    }

    @Test
    @DisplayName("Should delete user successfully with all related data")
    void testDeleteUser_WithValidUserId_ShouldDeleteUserAndRelatedData() {
        setUp();

        UUID userId = UUID.randomUUID();

        userDeletionService.deleteUser(userId);

        verify(cvService, times(1)).deleteAllCvsForUser(userId);
        verify(offerService, times(1)).deleteOffersByOwnerId(userId);
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("Should delete user CVs before deleting offers and user")
    void testDeleteUser_ShouldDeleteCvsFirst() {
        setUp();

        UUID userId = UUID.randomUUID();

        userDeletionService.deleteUser(userId);

        verify(cvService, times(1)).deleteAllCvsForUser(userId);
        verify(offerService, times(1)).deleteOffersByOwnerId(userId);
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    @DisplayName("Should throw BusinessException when CV deletion fails")
    void testDeleteUser_WhenCvDeletionFails_ShouldThrowBusinessException() {
        setUp();

        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.CV_NOT_FOUND))
            .when(cvService)
            .deleteAllCvsForUser(userId);

        assertThrows(
            BusinessException.class,
            () -> userDeletionService.deleteUser(userId),
            "Should throw BusinessException when CV deletion fails"
        );

        verify(cvService, times(1)).deleteAllCvsForUser(userId);
        verify(offerService, times(0)).deleteOffersByOwnerId(userId);
        verify(userService, times(0)).deleteUser(userId);
    }

    @Test
    @DisplayName("Should throw BusinessException when offer deletion fails")
    void testDeleteUser_WhenOfferDeletionFails_ShouldThrowBusinessException() {
        setUp();

        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.OFFER_NOT_FOUND))
            .when(offerService)
            .deleteOffersByOwnerId(userId);

        assertThrows(
            BusinessException.class,
            () -> userDeletionService.deleteUser(userId),
            "Should throw BusinessException when offer deletion fails"
        );

        verify(cvService, times(1)).deleteAllCvsForUser(userId);
        verify(offerService, times(1)).deleteOffersByOwnerId(userId);
        verify(userService, times(0)).deleteUser(userId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user deletion fails")
    void testDeleteUser_WhenUserDeletionFails_ShouldThrowBusinessException() {
        setUp();

        UUID userId = UUID.randomUUID();
        doThrow(new BusinessException(BusinessExceptionReason.USER_NOT_FOUND))
            .when(userService)
            .deleteUser(userId);

        assertThrows(
            BusinessException.class,
            () -> userDeletionService.deleteUser(userId),
            "Should throw BusinessException when user deletion fails"
        );

        verify(cvService, times(1)).deleteAllCvsForUser(userId);
        verify(offerService, times(1)).deleteOffersByOwnerId(userId);
        verify(userService, times(1)).deleteUser(userId);
    }
}
