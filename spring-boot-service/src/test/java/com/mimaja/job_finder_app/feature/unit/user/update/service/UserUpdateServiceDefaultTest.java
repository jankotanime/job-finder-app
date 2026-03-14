package com.mimaja.job_finder_app.feature.unit.user.update.service;

import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.createDefaultUserWithPasswordHash;
import static com.mimaja.job_finder_app.feature.unit.user.TestUserFixtures.createPrincipal;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.profilephoto.model.ProfilePhoto;
import com.mimaja.job_finder_app.feature.user.profilephoto.repository.ProfilePhotoRepository;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.feature.user.update.service.UserUpdateServiceDefault;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePhoneNumberRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateUserDataRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateEmailResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdatePhoneNumberResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateUserDataResponseDto;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.token.accessToken.dto.response.CreateAccessTokenResponseDto;
import com.mimaja.job_finder_app.security.token.accessToken.service.AccessTokenService;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
import com.mimaja.job_finder_app.shared.utils.CheckDataValidity;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserUpdateServiceDefault - Unit Tests")
public class UserUpdateServiceDefaultTest {
    private static final String CURRENT_HASHED_PASSWORD = "hashed-password";
    private static final String VALID_PASSWORD = "password";
    private static final String WRONG_PASSWORD = "wrongPassword";
    private static final String NEW_ACCESS_TOKEN = "new-access-token";
    private static final String NEW_USERNAME = "newusername";
    private static final String NEW_FIRST_NAME = "Jane";
    private static final String NEW_LAST_NAME = "Smith";
    private static final String NEW_PROFILE_DESCRIPTION = "New profile description";
    private static final int NEW_PHONE_NUMBER = 987654321;
    private static final String NEW_EMAIL = "newemail@example.com";
    private static final String INVALID_USERNAME = "invalid-username";
    private static final String INVALID_EMAIL = "invalid-email";

    @Mock
    private CheckDataValidity checkDataValidity;

    @Mock
    private PasswordConfiguration passwordConfiguration;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccessTokenService accessTokenService;

    @Mock
    private ProfilePhotoRepository profilePhotoRepository;

    @Mock
    private FileManagementService fileManagementService;

    private UserUpdateServiceDefault userUpdateService;

    private User testUser;

    private JwtPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        testUser = createDefaultUserWithPasswordHash(CURRENT_HASHED_PASSWORD);
        testPrincipal = createPrincipal(testUser);

        userUpdateService = new UserUpdateServiceDefault(
            checkDataValidity,
            passwordConfiguration,
            userRepository,
            accessTokenService,
            profilePhotoRepository,
            fileManagementService
        );
    }

    @Test
    @DisplayName("Should update user data successfully without profile photo")
    void testUpdateUserData_WithValidDataNoPhoto_ShouldReturnAccessToken() {
        UpdateUserDataRequestDto requestDto = new UpdateUserDataRequestDto(
            NEW_USERNAME,
            NEW_FIRST_NAME,
            NEW_LAST_NAME,
            NEW_PROFILE_DESCRIPTION,
            VALID_PASSWORD
        );

        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(NEW_ACCESS_TOKEN);

        when(passwordConfiguration.verifyPassword(VALID_PASSWORD, testUser.getPasswordHash())).thenReturn(true);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        UpdateUserDataResponseDto result = userUpdateService.updateUserdata(
            Optional.empty(),
            requestDto,
            testPrincipal
        );

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);
        assertThat(testUser)
            .extracting(
                User::getUsername,
                User::getFirstName,
                User::getLastName,
                User::getProfileDescription
            )
            .containsExactly(NEW_USERNAME, NEW_FIRST_NAME, NEW_LAST_NAME, NEW_PROFILE_DESCRIPTION);

        verify(passwordConfiguration, times(1)).verifyPassword(VALID_PASSWORD, testUser.getPasswordHash());
        verify(checkDataValidity, times(1)).checkUsername(testUser.getId(), NEW_USERNAME);
        verify(checkDataValidity, times(1)).checkRestData(NEW_FIRST_NAME);
        verify(checkDataValidity, times(1)).checkRestData(NEW_LAST_NAME);
        verify(checkDataValidity, times(1)).checkRestData(NEW_PROFILE_DESCRIPTION);
        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should update user data with new profile photo")
    void testUpdateUserData_WithValidDataAndNewPhoto_ShouldUpdatePhotoAndReturnToken() {
        UpdateUserDataRequestDto requestDto = new UpdateUserDataRequestDto(
            NEW_USERNAME,
            NEW_FIRST_NAME,
            NEW_LAST_NAME,
            NEW_PROFILE_DESCRIPTION,
            VALID_PASSWORD
        );

        MultipartFile mockPhoto = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails mockFileDetails = org.mockito.Mockito.mock(ProcessedFileDetails.class);

        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(NEW_ACCESS_TOKEN);

        when(passwordConfiguration.verifyPassword(VALID_PASSWORD, testUser.getPasswordHash())).thenReturn(true);
        when(fileManagementService.processFileDetails(any(), anyString())).thenReturn(mockFileDetails);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        UpdateUserDataResponseDto result = userUpdateService.updateUserdata(
            Optional.of(mockPhoto),
            requestDto,
            testPrincipal
        );

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);

        verify(passwordConfiguration, times(1)).verifyPassword(VALID_PASSWORD, testUser.getPasswordHash());
        verify(fileManagementService, times(1)).processFileDetails(any(), anyString());
        verify(fileManagementService, times(1)).uploadFile(mockFileDetails);
        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should remove existing profile photo when updating user data")
    void testUpdateUserData_WithExistingPhoto_ShouldDeleteOldPhotoAndAddNew() {
        ProfilePhoto existingPhoto = new ProfilePhoto();
        existingPhoto.setId(UUID.randomUUID());
        existingPhoto.setStorageKey("old-storage-key");
        testUser.setProfilePhoto(existingPhoto);

        UpdateUserDataRequestDto requestDto = new UpdateUserDataRequestDto(
            NEW_USERNAME,
            NEW_FIRST_NAME,
            NEW_LAST_NAME,
            NEW_PROFILE_DESCRIPTION,
            VALID_PASSWORD
        );

        MultipartFile mockPhoto = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails mockFileDetails = org.mockito.Mockito.mock(ProcessedFileDetails.class);

        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(NEW_ACCESS_TOKEN);

        when(passwordConfiguration.verifyPassword(VALID_PASSWORD, testUser.getPasswordHash())).thenReturn(true);
        when(fileManagementService.processFileDetails(any(), anyString())).thenReturn(mockFileDetails);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        UpdateUserDataResponseDto result = userUpdateService.updateUserdata(
            Optional.of(mockPhoto),
            requestDto,
            testPrincipal
        );

        assertNotNull(result, "Response DTO should not be null");

        verify(fileManagementService, times(1)).deleteFile("old-storage-key");
        verify(profilePhotoRepository, times(1)).deleteById(existingPhoto.getId());
        verify(fileManagementService, times(1)).uploadFile(mockFileDetails);
    }

    @Test
    @DisplayName("Should throw BusinessException when password is wrong")
    void testUpdateUserData_WithWrongPassword_ShouldThrowBusinessException() {
        UpdateUserDataRequestDto requestDto = new UpdateUserDataRequestDto(
            NEW_USERNAME,
            NEW_FIRST_NAME,
            NEW_LAST_NAME,
            NEW_PROFILE_DESCRIPTION,
            WRONG_PASSWORD
        );

        when(passwordConfiguration.verifyPassword(WRONG_PASSWORD, testUser.getPasswordHash())).thenReturn(false);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userUpdateService.updateUserdata(Optional.empty(), requestDto, testPrincipal),
            "Should throw BusinessException when password is wrong"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate wrong password")
            .isEqualTo(BusinessExceptionReason.WRONG_PASSWORD.getCode());

        verify(passwordConfiguration, times(1)).verifyPassword(WRONG_PASSWORD, testUser.getPasswordHash());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when username validation fails")
    void testUpdateUserData_WhenUsernameValidationFails_ShouldThrowBusinessException() {
        UpdateUserDataRequestDto requestDto = new UpdateUserDataRequestDto(
            INVALID_USERNAME,
            NEW_FIRST_NAME,
            NEW_LAST_NAME,
            NEW_PROFILE_DESCRIPTION,
            VALID_PASSWORD
        );

        when(passwordConfiguration.verifyPassword(VALID_PASSWORD, testUser.getPasswordHash())).thenReturn(true);
        doThrow(new BusinessException(BusinessExceptionReason.USERNAME_ALREADY_TAKEN))
            .when(checkDataValidity)
            .checkUsername(testUser.getId(), INVALID_USERNAME);

        assertThrows(
            BusinessException.class,
            () -> userUpdateService.updateUserdata(Optional.empty(), requestDto, testPrincipal),
            "Should throw BusinessException when username validation fails"
        );

        verify(checkDataValidity, times(1)).checkUsername(testUser.getId(), INVALID_USERNAME);
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should update phone number successfully")
    void testUpdatePhoneNumber_WithValidData_ShouldReturnAccessToken() {
        UpdatePhoneNumberRequestDto requestDto = new UpdatePhoneNumberRequestDto(
            NEW_PHONE_NUMBER,
            VALID_PASSWORD
        );

        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(NEW_ACCESS_TOKEN);

        when(passwordConfiguration.verifyPassword(VALID_PASSWORD, testUser.getPasswordHash())).thenReturn(true);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        UpdatePhoneNumberResponseDto result = userUpdateService.updatePhoneNumber(
            requestDto,
            testPrincipal
        );

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);

        verify(checkDataValidity, times(1)).checkPhoneNumber(testUser.getId(), NEW_PHONE_NUMBER);
        verify(passwordConfiguration, times(1)).verifyPassword(VALID_PASSWORD, testUser.getPasswordHash());
        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when phone number validation fails")
    void testUpdatePhoneNumber_WhenValidationFails_ShouldThrowBusinessException() {
        UpdatePhoneNumberRequestDto requestDto = new UpdatePhoneNumberRequestDto(
            NEW_PHONE_NUMBER,
            VALID_PASSWORD
        );

        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PHONE_NUMBER_PATTERN))
            .when(checkDataValidity)
            .checkPhoneNumber(testUser.getId(), NEW_PHONE_NUMBER);

        assertThrows(
            BusinessException.class,
            () -> userUpdateService.updatePhoneNumber(requestDto, testPrincipal),
            "Should throw BusinessException when phone number validation fails"
        );

        verify(checkDataValidity, times(1)).checkPhoneNumber(testUser.getId(), NEW_PHONE_NUMBER);
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when phone number update password is wrong")
    void testUpdatePhoneNumber_WithWrongPassword_ShouldThrowBusinessException() {
        UpdatePhoneNumberRequestDto requestDto = new UpdatePhoneNumberRequestDto(
            NEW_PHONE_NUMBER,
            WRONG_PASSWORD
        );

        when(passwordConfiguration.verifyPassword(WRONG_PASSWORD, testUser.getPasswordHash())).thenReturn(false);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userUpdateService.updatePhoneNumber(requestDto, testPrincipal),
            "Should throw BusinessException when password is wrong"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate wrong password")
            .isEqualTo(BusinessExceptionReason.WRONG_PASSWORD.getCode());

        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should update email successfully")
    void testUpdateEmail_WithValidData_ShouldReturnAccessToken() {
        UpdateEmailRequestDto requestDto = new UpdateEmailRequestDto(
            NEW_EMAIL,
            VALID_PASSWORD
        );

        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(NEW_ACCESS_TOKEN);

        when(passwordConfiguration.verifyPassword(VALID_PASSWORD, testUser.getPasswordHash())).thenReturn(true);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        UpdateEmailResponseDto result = userUpdateService.updateEmail(
            requestDto,
            testPrincipal
        );

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);

        verify(checkDataValidity, times(1)).checkEmail(testUser.getId(), NEW_EMAIL);
        verify(passwordConfiguration, times(1)).verifyPassword(VALID_PASSWORD, testUser.getPasswordHash());
        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when email validation fails")
    void testUpdateEmail_WhenValidationFails_ShouldThrowBusinessException() {
        UpdateEmailRequestDto requestDto = new UpdateEmailRequestDto(
            INVALID_EMAIL,
            VALID_PASSWORD
        );

        doThrow(new BusinessException(BusinessExceptionReason.EMAIL_ALREADY_TAKEN))
            .when(checkDataValidity)
            .checkEmail(testUser.getId(), INVALID_EMAIL);

        assertThrows(
            BusinessException.class,
            () -> userUpdateService.updateEmail(requestDto, testPrincipal),
            "Should throw BusinessException when email validation fails"
        );

        verify(checkDataValidity, times(1)).checkEmail(testUser.getId(), INVALID_EMAIL);
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when email update password is wrong")
    void testUpdateEmail_WithWrongPassword_ShouldThrowBusinessException() {
        UpdateEmailRequestDto requestDto = new UpdateEmailRequestDto(
            NEW_EMAIL,
            WRONG_PASSWORD
        );

        when(passwordConfiguration.verifyPassword(WRONG_PASSWORD, testUser.getPasswordHash())).thenReturn(false);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userUpdateService.updateEmail(requestDto, testPrincipal),
            "Should throw BusinessException when password is wrong"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate wrong password")
            .isEqualTo(BusinessExceptionReason.WRONG_PASSWORD.getCode());

        verify(userRepository, times(0)).save(any());
    }
}
