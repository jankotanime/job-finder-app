package com.mimaja.job_finder_app.feature.unit.security.authorization.login.utils;

import static com.mimaja.job_finder_app.feature.unit.security.mockdata.SecurityMockData.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.authorization.login.utils.DefaultLoginValidation;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultLoginValidation - Unit Tests")
class DefaultLoginValidationTest {

    @Mock private UserRepository userRepository;

    private DefaultLoginValidation defaultLoginValidation;

    private User testUser;

    @BeforeEach
    void setUp() {
        defaultLoginValidation = new DefaultLoginValidation(userRepository);
        testUser = createTestUser();
    }

    // =========================
    // userValidation - By Username Tests
    // =========================

    @Test
    @DisplayName("Should return user when found by username")
    void testUserValidation_shouldReturnUser_whenUserFoundByUsername() {
        // given
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        // when
        User result = defaultLoginValidation.userValidation(TEST_USERNAME);

        // then
        assertThat(result).as("User should be found by username").isNotNull();
        assertThat(result.getId()).as("User ID should match").isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should call findByUsername when validating user")
    void testUserValidation_shouldCallFindByUsername_whenValidatingUser() {
        // given
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        // when
        defaultLoginValidation.userValidation(TEST_USERNAME);

        // then
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    // =========================
    // userValidation - By Email Tests
    // =========================

    @Test
    @DisplayName("Should find user by email when username not found")
    void testUserValidation_shouldReturnUser_whenUserFoundByEmail() {
        // given
        when(userRepository.findByUsername(TEST_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // when
        User result = defaultLoginValidation.userValidation(TEST_EMAIL);

        // then
        assertThat(result).as("User should be found by email").isNotNull();
        assertThat(result.getId()).as("User ID should match").isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should call findByEmail when username search returns empty")
    void testUserValidation_shouldCallFindByEmail_whenUsernameNotFound() {
        // given
        when(userRepository.findByUsername(TEST_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // when
        defaultLoginValidation.userValidation(TEST_EMAIL);

        // then
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("Should not call findByEmail when user found by username")
    void testUserValidation_shouldNotCallFindByEmail_whenUserFoundByUsername() {
        // given
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        // when
        defaultLoginValidation.userValidation(TEST_USERNAME);

        // then
        verify(userRepository, never()).findByEmail(anyString());
    }

    // =========================
    // userValidation - By Phone Number Tests
    // =========================

    @Test
    @DisplayName("Should find user by phone number when username and email not found")
    void testUserValidation_shouldReturnUser_whenUserFoundByPhoneNumber() {
        // given
        String phoneNumberString = String.valueOf(TEST_PHONE_NUMBER);
        when(userRepository.findByUsername(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(TEST_PHONE_NUMBER))
                .thenReturn(Optional.of(testUser));

        // when
        User result = defaultLoginValidation.userValidation(phoneNumberString);

        // then
        assertThat(result).as("User should be found by phone number").isNotNull();
        assertThat(result.getId()).as("User ID should match").isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should call findByPhoneNumber when username and email searches return empty")
    void testUserValidation_shouldCallFindByPhoneNumber_whenNotFoundByUsernameAndEmail() {
        // given
        String phoneNumberString = String.valueOf(TEST_PHONE_NUMBER);
        when(userRepository.findByUsername(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(TEST_PHONE_NUMBER))
                .thenReturn(Optional.of(testUser));

        // when
        defaultLoginValidation.userValidation(phoneNumberString);

        // then
        verify(userRepository, times(1)).findByPhoneNumber(TEST_PHONE_NUMBER);
    }

    @Test
    @DisplayName("Should not call findByPhoneNumber when user found by email")
    void testUserValidation_shouldNotCallFindByPhoneNumber_whenUserFoundByEmail() {
        // given
        when(userRepository.findByUsername(TEST_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        // when
        defaultLoginValidation.userValidation(TEST_EMAIL);

        // then
        verify(userRepository, never()).findByPhoneNumber(anyInt());
    }

    // =========================
    // userValidation - Valid Phone Number But User Not Found Tests
    // =========================

    @Test
    @DisplayName("Should throw WRONG_LOGIN_DATA when phone number is valid but user not found")
    void testUserValidation_shouldThrowBusinessException_whenPhoneNumberValidButUserNotFound() {
        // given
        String phoneNumberString = String.valueOf(TEST_PHONE_NUMBER);
        when(userRepository.findByUsername(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(TEST_PHONE_NUMBER)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> defaultLoginValidation.userValidation(phoneNumberString))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException when phone number is valid but user not found");
    }

    @Test
    @DisplayName("Should throw correct exception code when phone number valid but user not found")
    void testUserValidation_shouldThrowCorrectExceptionCode_whenPhoneNumberValidButUserNotFound() {
        // given
        String phoneNumberString = String.valueOf(TEST_PHONE_NUMBER);
        when(userRepository.findByUsername(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(phoneNumberString)).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(TEST_PHONE_NUMBER)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> defaultLoginValidation.userValidation(phoneNumberString));

        assertThat(exception.getMessage())
                .as("Exception reason should be WRONG_LOGIN_DATA")
                .isEqualTo(BusinessExceptionReason.WRONG_LOGIN_DATA.getMessage());
    }

    // =========================
    // userValidation - Invalid Phone Number Format Tests
    // =========================

    @Test
    @DisplayName("Should throw WRONG_LOGIN_DATA when phone number format is invalid")
    void testUserValidation_shouldThrowBusinessException_whenPhoneNumberFormatInvalid() {
        // given
        String invalidPhoneNumber = "notanumber";
        when(userRepository.findByUsername(invalidPhoneNumber)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(invalidPhoneNumber)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> defaultLoginValidation.userValidation(invalidPhoneNumber))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException for invalid phone number format");
    }

    @Test
    @DisplayName("Should throw correct exception code when phone number format is invalid")
    void testUserValidation_shouldThrowCorrectExceptionCode_whenPhoneNumberFormatInvalid() {
        // given
        String invalidPhoneNumber = "notanumber";
        when(userRepository.findByUsername(invalidPhoneNumber)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(invalidPhoneNumber)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> defaultLoginValidation.userValidation(invalidPhoneNumber));

        assertThat(exception.getMessage())
                .as("Exception reason should be WRONG_LOGIN_DATA")
                .isEqualTo(BusinessExceptionReason.WRONG_LOGIN_DATA.getMessage());
    }

    @Test
    @DisplayName("Should throw WRONG_LOGIN_DATA when loginData contains special characters")
    void testUserValidation_shouldThrowBusinessException_whenLoginDataContainsSpecialCharacters() {
        // given
        String invalidLoginData = "user@#$%";
        when(userRepository.findByUsername(invalidLoginData)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(invalidLoginData)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> defaultLoginValidation.userValidation(invalidLoginData))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException for login data with special characters");
    }

    // =========================
    // userValidation - User Not Found Tests
    // =========================

    @Test
    @DisplayName("Should throw WRONG_LOGIN_DATA when user not found in any repository search")
    void testUserValidation_shouldThrowBusinessException_whenUserNotFound() {
        // given
        String nonexistentEmail = "nonexistent@example.com";
        when(userRepository.findByUsername(nonexistentEmail)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(nonexistentEmail)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> defaultLoginValidation.userValidation(nonexistentEmail))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException when user not found");
    }

    @Test
    @DisplayName("Should throw correct exception code when user not found")
    void testUserValidation_shouldThrowCorrectExceptionCode_whenUserNotFound() {
        // given
        String nonexistentEmail = "nonexistent@example.com";
        when(userRepository.findByUsername(nonexistentEmail)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(nonexistentEmail)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> defaultLoginValidation.userValidation(nonexistentEmail));

        assertThat(exception.getMessage())
                .as("Exception reason should be WRONG_LOGIN_DATA")
                .isEqualTo(BusinessExceptionReason.WRONG_LOGIN_DATA.getMessage());
    }

    // =========================
    // userValidation - Edge Cases Tests
    // =========================

    @Test
    @DisplayName("Should throw WRONG_LOGIN_DATA when loginData is empty string")
    void testUserValidation_shouldThrowBusinessException_whenLoginDataIsEmpty() {
        // given
        String emptyLoginData = "";
        when(userRepository.findByUsername(emptyLoginData)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(emptyLoginData)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> defaultLoginValidation.userValidation(emptyLoginData))
                .isInstanceOf(BusinessException.class)
                .as("Should throw BusinessException for empty login data");
    }

    @Test
    @DisplayName("Should return correct user when multiple searches needed")
    void testUserValidation_shouldReturnCorrectUser_whenMultipleSearchesNeeded() {
        // given - setup multiple users to ensure correct one is returned
        User emailUser = createTestUser();
        emailUser.setId(UUID.randomUUID());
        
        when(userRepository.findByUsername(TEST_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(emailUser));

        // when
        User result = defaultLoginValidation.userValidation(TEST_EMAIL);

        // then
        assertThat(result.getId())
                .as("Should return user found by email")
                .isEqualTo(emailUser.getId());
    }
}
