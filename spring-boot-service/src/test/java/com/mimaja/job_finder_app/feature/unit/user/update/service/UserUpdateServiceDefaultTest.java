package com.mimaja.job_finder_app.feature.unit.user.update.service;

import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.TEST_CURRENT_PASSWORD;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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
class UserUpdateServiceDefaultTest {

    private static final String TEST_HASHED_PASSWORD = "hashed-password";
    private static final String TEST_NEW_USERNAME    = "newusername";
    private static final String TEST_NEW_EMAIL       = "newemail@example.com";
    private static final int    TEST_NEW_PHONE       = 987654321;
    private static final String TEST_NEW_ACCESS_TOKEN = "new-access-token";
    private static final String TEST_WRONG_PASSWORD  = "wrongPassword";

    @Mock private CheckDataValidity checkDataValidity;
    @Mock private PasswordConfiguration passwordConfiguration;
    @Mock private UserRepository userRepository;
    @Mock private AccessTokenService accessTokenService;
    @Mock private ProfilePhotoRepository profilePhotoRepository;
    @Mock private FileManagementService fileManagementService;

    private UserUpdateServiceDefault userUpdateService;
    private User testUser;
    private JwtPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testUser.setPasswordHash(TEST_HASHED_PASSWORD);
        testUser.setProfilePhoto(null);
        testPrincipal = JwtPrincipal.from(testUser);
        userUpdateService = new UserUpdateServiceDefault(
                checkDataValidity, passwordConfiguration, userRepository,
                accessTokenService, profilePhotoRepository, fileManagementService);
    }

    @Test
    void updateUserdata_shouldReturnNonNullResponse_whenDataIsValidWithNoPhoto() {
        setupValidUserDataMocks();
        UpdateUserDataResponseDto result = userUpdateService.updateUserdata(
                Optional.empty(), createValidUserDataRequest(), testPrincipal);
        assertThat(result).isNotNull();
    }

    @Test
    void updateUserdata_shouldReturnCorrectAccessToken_whenDataIsValidWithNoPhoto() {
        setupValidUserDataMocks();
        UpdateUserDataResponseDto result = userUpdateService.updateUserdata(
                Optional.empty(), createValidUserDataRequest(), testPrincipal);
        assertThat(result.accessToken()).isEqualTo(TEST_NEW_ACCESS_TOKEN);
    }

    @Test
    void updateUserdata_shouldVerifyPassword_whenUpdatingUserData() {
        setupValidUserDataMocks();
        userUpdateService.updateUserdata(Optional.empty(), createValidUserDataRequest(), testPrincipal);
        verify(passwordConfiguration, times(1)).verifyPassword(TEST_CURRENT_PASSWORD, TEST_HASHED_PASSWORD);
    }

    @Test
    void updateUserdata_shouldCheckUsername_whenUpdatingUserData() {
        setupValidUserDataMocks();
        userUpdateService.updateUserdata(Optional.empty(), createValidUserDataRequest(), testPrincipal);
        verify(checkDataValidity, times(1)).checkUsername(testUser.getId(), TEST_NEW_USERNAME);
    }

    @Test
    void updateUserdata_shouldSaveUser_whenUpdatingUserData() {
        setupValidUserDataMocks();
        userUpdateService.updateUserdata(Optional.empty(), createValidUserDataRequest(), testPrincipal);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateUserdata_shouldCallCreateToken_whenUpdatingUserData() {
        setupValidUserDataMocks();
        userUpdateService.updateUserdata(Optional.empty(), createValidUserDataRequest(), testPrincipal);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    void updateUserdata_shouldProcessFile_whenPhotoProvided() {
        setupValidUserDataMocks();
        ProcessedFileDetails mockFileDetails = createMockFileDetails();
        when(fileManagementService.processFileDetails(any(), anyString())).thenReturn(mockFileDetails);
        userUpdateService.updateUserdata(
                Optional.of(createMockPhoto()), createValidUserDataRequest(), testPrincipal);
        verify(fileManagementService, times(1)).processFileDetails(any(), anyString());
    }

    @Test
    void updateUserdata_shouldUploadFile_whenPhotoProvided() {
        setupValidUserDataMocks();
        ProcessedFileDetails mockFileDetails = createMockFileDetails();
        when(fileManagementService.processFileDetails(any(), anyString())).thenReturn(mockFileDetails);
        userUpdateService.updateUserdata(
                Optional.of(createMockPhoto()), createValidUserDataRequest(), testPrincipal);
        verify(fileManagementService, times(1)).uploadFile(mockFileDetails);
    }

    @Test
    void updateUserdata_shouldDeleteOldPhoto_whenExistingPhotoAndNewPhotoProvided() {
        ProfilePhoto existingPhoto = createExistingPhoto();
        testUser.setProfilePhoto(existingPhoto);
        setupValidUserDataMocks();
        ProcessedFileDetails mockFileDetails = createMockFileDetails();
        when(fileManagementService.processFileDetails(any(), anyString())).thenReturn(mockFileDetails);
        userUpdateService.updateUserdata(
                Optional.of(createMockPhoto()), createValidUserDataRequest(), testPrincipal);
        verify(fileManagementService, times(1)).deleteFile("old-storage-key");
    }

    @Test
    void updateUserdata_shouldDeleteOldPhotoFromRepository_whenExistingPhotoAndNewPhotoProvided() {
        ProfilePhoto existingPhoto = createExistingPhoto();
        testUser.setProfilePhoto(existingPhoto);
        setupValidUserDataMocks();
        ProcessedFileDetails mockFileDetails = createMockFileDetails();
        when(fileManagementService.processFileDetails(any(), anyString())).thenReturn(mockFileDetails);
        userUpdateService.updateUserdata(
                Optional.of(createMockPhoto()), createValidUserDataRequest(), testPrincipal);
        verify(profilePhotoRepository, times(1)).deleteById(existingPhoto.getId());
    }

    @Test
    void updateUserdata_shouldThrowExceptionWithWrongPasswordCode_whenPasswordIsWrong() {
        when(passwordConfiguration.verifyPassword(TEST_WRONG_PASSWORD, TEST_HASHED_PASSWORD)).thenReturn(false);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userUpdateService.updateUserdata(
                        Optional.empty(), createUserDataRequestWithPassword(TEST_WRONG_PASSWORD), testPrincipal));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.WRONG_PASSWORD.getCode());
    }

    @Test
    void updateUserdata_shouldNotSaveUser_whenPasswordIsWrong() {
        when(passwordConfiguration.verifyPassword(TEST_WRONG_PASSWORD, TEST_HASHED_PASSWORD)).thenReturn(false);
        assertThrows(BusinessException.class,
                () -> userUpdateService.updateUserdata(
                        Optional.empty(), createUserDataRequestWithPassword(TEST_WRONG_PASSWORD), testPrincipal));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserdata_shouldThrowBusinessException_whenUsernameValidationFails() {
        when(passwordConfiguration.verifyPassword(TEST_CURRENT_PASSWORD, TEST_HASHED_PASSWORD)).thenReturn(true);
        doThrow(new BusinessException(BusinessExceptionReason.USERNAME_ALREADY_TAKEN))
                .when(checkDataValidity).checkUsername(testUser.getId(), TEST_NEW_USERNAME);
        assertThrows(BusinessException.class,
                () -> userUpdateService.updateUserdata(
                        Optional.empty(), createValidUserDataRequest(), testPrincipal));
    }

    @Test
    void updateUserdata_shouldNotSaveUser_whenUsernameValidationFails() {
        when(passwordConfiguration.verifyPassword(TEST_CURRENT_PASSWORD, TEST_HASHED_PASSWORD)).thenReturn(true);
        doThrow(new BusinessException(BusinessExceptionReason.USERNAME_ALREADY_TAKEN))
                .when(checkDataValidity).checkUsername(testUser.getId(), TEST_NEW_USERNAME);
        assertThrows(BusinessException.class,
                () -> userUpdateService.updateUserdata(
                        Optional.empty(), createValidUserDataRequest(), testPrincipal));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePhoneNumber_shouldReturnCorrectAccessToken_whenDataIsValid() {
        setupValidPhoneNumberMocks();
        UpdatePhoneNumberResponseDto result = userUpdateService.updatePhoneNumber(
                createValidPhoneRequest(), testPrincipal);
        assertThat(result.accessToken()).isEqualTo(TEST_NEW_ACCESS_TOKEN);
    }

    @Test
    void updatePhoneNumber_shouldCheckPhoneNumber_whenUpdatingPhoneNumber() {
        setupValidPhoneNumberMocks();
        userUpdateService.updatePhoneNumber(createValidPhoneRequest(), testPrincipal);
        verify(checkDataValidity, times(1)).checkPhoneNumber(testUser.getId(), TEST_NEW_PHONE);
    }

    @Test
    void updatePhoneNumber_shouldSaveUser_whenUpdatingPhoneNumber() {
        setupValidPhoneNumberMocks();
        userUpdateService.updatePhoneNumber(createValidPhoneRequest(), testPrincipal);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updatePhoneNumber_shouldThrowBusinessException_whenPhoneValidationFails() {
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PHONE_NUMBER_PATTERN))
                .when(checkDataValidity).checkPhoneNumber(testUser.getId(), TEST_NEW_PHONE);
        assertThrows(BusinessException.class,
                () -> userUpdateService.updatePhoneNumber(createValidPhoneRequest(), testPrincipal));
    }

    @Test
    void updatePhoneNumber_shouldNotSaveUser_whenPhoneValidationFails() {
        doThrow(new BusinessException(BusinessExceptionReason.INVALID_PHONE_NUMBER_PATTERN))
                .when(checkDataValidity).checkPhoneNumber(testUser.getId(), TEST_NEW_PHONE);
        assertThrows(BusinessException.class,
                () -> userUpdateService.updatePhoneNumber(createValidPhoneRequest(), testPrincipal));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePhoneNumber_shouldThrowExceptionWithWrongPasswordCode_whenPasswordIsWrong() {
        when(passwordConfiguration.verifyPassword(TEST_WRONG_PASSWORD, TEST_HASHED_PASSWORD)).thenReturn(false);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userUpdateService.updatePhoneNumber(
                        new UpdatePhoneNumberRequestDto(TEST_NEW_PHONE, TEST_WRONG_PASSWORD), testPrincipal));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.WRONG_PASSWORD.getCode());
    }

    @Test
    void updateEmail_shouldReturnCorrectAccessToken_whenDataIsValid() {
        setupValidEmailMocks();
        UpdateEmailResponseDto result = userUpdateService.updateEmail(createValidEmailRequest(), testPrincipal);
        assertThat(result.accessToken()).isEqualTo(TEST_NEW_ACCESS_TOKEN);
    }

    @Test
    void updateEmail_shouldCheckEmail_whenUpdatingEmail() {
        setupValidEmailMocks();
        userUpdateService.updateEmail(createValidEmailRequest(), testPrincipal);
        verify(checkDataValidity, times(1)).checkEmail(testUser.getId(), TEST_NEW_EMAIL);
    }

    @Test
    void updateEmail_shouldSaveUser_whenUpdatingEmail() {
        setupValidEmailMocks();
        userUpdateService.updateEmail(createValidEmailRequest(), testPrincipal);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateEmail_shouldThrowBusinessException_whenEmailValidationFails() {
        doThrow(new BusinessException(BusinessExceptionReason.EMAIL_ALREADY_TAKEN))
                .when(checkDataValidity).checkEmail(testUser.getId(), "invalid-email");
        assertThrows(BusinessException.class,
                () -> userUpdateService.updateEmail(
                        new UpdateEmailRequestDto("invalid-email", TEST_CURRENT_PASSWORD), testPrincipal));
    }

    @Test
    void updateEmail_shouldNotSaveUser_whenEmailValidationFails() {
        doThrow(new BusinessException(BusinessExceptionReason.EMAIL_ALREADY_TAKEN))
                .when(checkDataValidity).checkEmail(testUser.getId(), "invalid-email");
        assertThrows(BusinessException.class,
                () -> userUpdateService.updateEmail(
                        new UpdateEmailRequestDto("invalid-email", TEST_CURRENT_PASSWORD), testPrincipal));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateEmail_shouldThrowExceptionWithWrongPasswordCode_whenPasswordIsWrong() {
        when(passwordConfiguration.verifyPassword(TEST_WRONG_PASSWORD, TEST_HASHED_PASSWORD)).thenReturn(false);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userUpdateService.updateEmail(
                        new UpdateEmailRequestDto(TEST_NEW_EMAIL, TEST_WRONG_PASSWORD), testPrincipal));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.WRONG_PASSWORD.getCode());
    }

    private void setupValidUserDataMocks() {
        when(passwordConfiguration.verifyPassword(TEST_CURRENT_PASSWORD, TEST_HASHED_PASSWORD)).thenReturn(true);
        when(accessTokenService.createToken(testUser))
                .thenReturn(new CreateAccessTokenResponseDto(TEST_NEW_ACCESS_TOKEN));
    }

    private void setupValidPhoneNumberMocks() {
        when(passwordConfiguration.verifyPassword(TEST_CURRENT_PASSWORD, TEST_HASHED_PASSWORD)).thenReturn(true);
        when(accessTokenService.createToken(testUser))
                .thenReturn(new CreateAccessTokenResponseDto(TEST_NEW_ACCESS_TOKEN));
    }

    private void setupValidEmailMocks() {
        when(passwordConfiguration.verifyPassword(TEST_CURRENT_PASSWORD, TEST_HASHED_PASSWORD)).thenReturn(true);
        when(accessTokenService.createToken(testUser))
                .thenReturn(new CreateAccessTokenResponseDto(TEST_NEW_ACCESS_TOKEN));
    }

    private MultipartFile createMockPhoto() {
        return org.mockito.Mockito.mock(MultipartFile.class);
    }

    private ProcessedFileDetails createMockFileDetails() {
        return org.mockito.Mockito.mock(ProcessedFileDetails.class);
    }

    private ProfilePhoto createExistingPhoto() {
        ProfilePhoto photo = new ProfilePhoto();
        photo.setId(UUID.randomUUID());
        photo.setStorageKey("old-storage-key");
        return photo;
    }

    private UpdateUserDataRequestDto createValidUserDataRequest() {
        return new UpdateUserDataRequestDto(TEST_NEW_USERNAME, "Jane", "Smith", "New description", TEST_CURRENT_PASSWORD);
    }

    private UpdateUserDataRequestDto createUserDataRequestWithPassword(String password) {
        return new UpdateUserDataRequestDto(TEST_NEW_USERNAME, "Jane", "Smith", "New description", password);
    }

    private UpdatePhoneNumberRequestDto createValidPhoneRequest() {
        return new UpdatePhoneNumberRequestDto(TEST_NEW_PHONE, TEST_CURRENT_PASSWORD);
    }

    private UpdateEmailRequestDto createValidEmailRequest() {
        return new UpdateEmailRequestDto(TEST_NEW_EMAIL, TEST_CURRENT_PASSWORD);
    }
}
