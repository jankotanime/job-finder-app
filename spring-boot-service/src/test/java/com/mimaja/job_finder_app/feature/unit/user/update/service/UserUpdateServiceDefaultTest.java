package com.mimaja.job_finder_app.feature.unit.user.update.service;

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

    void setUp() {
        testUser = createTestUser();
        testPrincipal = createTestPrincipal(testUser);

        userUpdateService = new UserUpdateServiceDefault(
            checkDataValidity,
            passwordConfiguration,
            userRepository,
            accessTokenService,
            profilePhotoRepository,
            fileManagementService
        );
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("user@example.com");
        user.setPhoneNumber(123456789);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setProfileDescription("Test profile description");
        user.setPasswordHash("hashed-password");
        user.setProfilePhoto(null);
        return user;
    }

    private JwtPrincipal createTestPrincipal(User user) {
        return JwtPrincipal.from(user);
    }

    @Test
    @DisplayName("Should update user data successfully without profile photo")
    void testUpdateUserdata_WithValidDataNoPhoto_ShouldReturnAccessToken() {
        setUp();

        UpdateUserDataRequestDto requestDto = new UpdateUserDataRequestDto(
            "newusername",
            "Jane",
            "Smith",
            "New profile description",
            "password"
        );

        String newAccessToken = "new-access-token";
        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(newAccessToken);

        when(passwordConfiguration.verifyPassword("password", testUser.getPasswordHash())).thenReturn(true);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        UpdateUserDataResponseDto result = userUpdateService.updateUserdata(
            Optional.empty(),
            requestDto,
            testPrincipal
        );

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.accessToken()).isEqualTo(newAccessToken);

        verify(passwordConfiguration, times(1)).verifyPassword("password", testUser.getPasswordHash());
        verify(checkDataValidity, times(1)).checkUsername(testUser.getId(), "newusername");
        verify(checkDataValidity, times(1)).checkRestData("Jane");
        verify(checkDataValidity, times(1)).checkRestData("Smith");
        verify(checkDataValidity, times(1)).checkRestData("New profile description");
        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should update user data with new profile photo")
    void testUpdateUserdata_WithValidDataAndNewPhoto_ShouldUpdatePhotoAndReturnToken() {
        setUp();

        UpdateUserDataRequestDto requestDto = new UpdateUserDataRequestDto(
            "newusername",
            "Jane",
            "Smith",
            "New profile description",
            "password"
        );

        MultipartFile mockPhoto = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails mockFileDetails = org.mockito.Mockito.mock(ProcessedFileDetails.class);

        String newAccessToken = "new-access-token";
        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(newAccessToken);

        when(passwordConfiguration.verifyPassword("password", testUser.getPasswordHash())).thenReturn(true);
        when(fileManagementService.processFileDetails(any(), anyString())).thenReturn(mockFileDetails);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        UpdateUserDataResponseDto result = userUpdateService.updateUserdata(
            Optional.of(mockPhoto),
            requestDto,
            testPrincipal
        );

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.accessToken()).isEqualTo(newAccessToken);

        verify(passwordConfiguration, times(1)).verifyPassword("password", testUser.getPasswordHash());
        verify(fileManagementService, times(1)).processFileDetails(any(), anyString());
        verify(fileManagementService, times(1)).uploadFile(mockFileDetails);
        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should remove existing profile photo when updating user data")
    void testUpdateUserdata_WithExistingPhoto_ShouldDeleteOldPhotoAndAddNew() {
        setUp();

        ProfilePhoto existingPhoto = new ProfilePhoto();
        existingPhoto.setId(UUID.randomUUID());
        existingPhoto.setStorageKey("old-storage-key");
        testUser.setProfilePhoto(existingPhoto);

        UpdateUserDataRequestDto requestDto = new UpdateUserDataRequestDto(
            "newusername",
            "Jane",
            "Smith",
            "New profile description",
            "password"
        );

        MultipartFile mockPhoto = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails mockFileDetails = org.mockito.Mockito.mock(ProcessedFileDetails.class);

        String newAccessToken = "new-access-token";
        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(newAccessToken);

        when(passwordConfiguration.verifyPassword("password", testUser.getPasswordHash())).thenReturn(true);
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
    void testUpdateUserdata_WithWrongPassword_ShouldThrowBusinessException() {
        setUp();

        UpdateUserDataRequestDto requestDto = new UpdateUserDataRequestDto(
            "newusername",
            "Jane",
            "Smith",
            "New profile description",
            "wrongPassword"
        );

        when(passwordConfiguration.verifyPassword("wrongPassword", testUser.getPasswordHash())).thenReturn(false);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userUpdateService.updateUserdata(Optional.empty(), requestDto, testPrincipal),
            "Should throw BusinessException when password is wrong"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate wrong password")
            .isEqualTo(BusinessExceptionReason.WRONG_PASSWORD.getCode());

        verify(passwordConfiguration, times(1)).verifyPassword("wrongPassword", testUser.getPasswordHash());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when username validation fails")
    void testUpdateUserdata_WhenUsernameValidationFails_ShouldThrowBusinessException() {
        setUp();

        UpdateUserDataRequestDto requestDto = new UpdateUserDataRequestDto(
            "invalid-username",
            "Jane",
            "Smith",
            "New profile description",
            "password"
        );

        when(passwordConfiguration.verifyPassword("password", testUser.getPasswordHash())).thenReturn(true);
        doThrow(new BusinessException(BusinessExceptionReason.USERNAME_ALREADY_TAKEN))
            .when(checkDataValidity)
            .checkUsername(testUser.getId(), "invalid-username");

        assertThrows(
            BusinessException.class,
            () -> userUpdateService.updateUserdata(Optional.empty(), requestDto, testPrincipal),
            "Should throw BusinessException when username validation fails"
        );

        verify(checkDataValidity, times(1)).checkUsername(testUser.getId(), "invalid-username");
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should update phone number successfully")
    void testUpdatePhoneNumber_WithValidData_ShouldReturnAccessToken() {
        setUp();

        UpdatePhoneNumberRequestDto requestDto = new UpdatePhoneNumberRequestDto(
            987654321,
            "password"
        );

        String newAccessToken = "new-access-token";
        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(newAccessToken);

        when(passwordConfiguration.verifyPassword("password", testUser.getPasswordHash())).thenReturn(true);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        UpdatePhoneNumberResponseDto result = userUpdateService.updatePhoneNumber(
            requestDto,
            testPrincipal
        );

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.accessToken()).isEqualTo(newAccessToken);

        verify(checkDataValidity, times(1)).checkPhoneNumber(testUser.getId(), 987654321);
        verify(passwordConfiguration, times(1)).verifyPassword("password", testUser.getPasswordHash());
        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when phone number validation fails")
    void testUpdatePhoneNumber_WhenValidationFails_ShouldThrowBusinessException() {
        setUp();

        UpdatePhoneNumberRequestDto requestDto = new UpdatePhoneNumberRequestDto(
            987654321,
            "password"
        );

        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PHONE_NUMBER_PATTERN))
            .when(checkDataValidity)
            .checkPhoneNumber(testUser.getId(), 987654321);

        assertThrows(
            BusinessException.class,
            () -> userUpdateService.updatePhoneNumber(requestDto, testPrincipal),
            "Should throw BusinessException when phone number validation fails"
        );

        verify(checkDataValidity, times(1)).checkPhoneNumber(testUser.getId(), 987654321);
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when phone number update password is wrong")
    void testUpdatePhoneNumber_WithWrongPassword_ShouldThrowBusinessException() {
        setUp();

        UpdatePhoneNumberRequestDto requestDto = new UpdatePhoneNumberRequestDto(
            987654321,
            "wrongPassword"
        );

        when(passwordConfiguration.verifyPassword("wrongPassword", testUser.getPasswordHash())).thenReturn(false);

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
        setUp();

        UpdateEmailRequestDto requestDto = new UpdateEmailRequestDto(
            "newemail@example.com",
            "password"
        );

        String newAccessToken = "new-access-token";
        CreateAccessTokenResponseDto tokenDto = new CreateAccessTokenResponseDto(newAccessToken);

        when(passwordConfiguration.verifyPassword("password", testUser.getPasswordHash())).thenReturn(true);
        when(accessTokenService.createToken(testUser)).thenReturn(tokenDto);

        UpdateEmailResponseDto result = userUpdateService.updateEmail(
            requestDto,
            testPrincipal
        );

        assertNotNull(result, "Response DTO should not be null");
        assertThat(result.accessToken()).isEqualTo(newAccessToken);

        verify(checkDataValidity, times(1)).checkEmail(testUser.getId(), "newemail@example.com");
        verify(passwordConfiguration, times(1)).verifyPassword("password", testUser.getPasswordHash());
        verify(userRepository, times(1)).save(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when email validation fails")
    void testUpdateEmail_WhenValidationFails_ShouldThrowBusinessException() {
        setUp();

        UpdateEmailRequestDto requestDto = new UpdateEmailRequestDto(
            "invalid-email",
            "password"
        );

        doThrow(new BusinessException(BusinessExceptionReason.EMAIL_ALREADY_TAKEN))
            .when(checkDataValidity)
            .checkEmail(testUser.getId(), "invalid-email");

        assertThrows(
            BusinessException.class,
            () -> userUpdateService.updateEmail(requestDto, testPrincipal),
            "Should throw BusinessException when email validation fails"
        );

        verify(checkDataValidity, times(1)).checkEmail(testUser.getId(), "invalid-email");
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when email update password is wrong")
    void testUpdateEmail_WithWrongPassword_ShouldThrowBusinessException() {
        setUp();

        UpdateEmailRequestDto requestDto = new UpdateEmailRequestDto(
            "newemail@example.com",
            "wrongPassword"
        );

        when(passwordConfiguration.verifyPassword("wrongPassword", testUser.getPasswordHash())).thenReturn(false);

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
