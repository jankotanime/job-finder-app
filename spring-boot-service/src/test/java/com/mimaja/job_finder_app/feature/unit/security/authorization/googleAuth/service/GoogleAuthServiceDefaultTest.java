package com.mimaja.job_finder_app.feature.unit.security.authorization.googleAuth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthCheckExistenceRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthLoginRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthRegisterRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.response.GoogleAuthLoginResponseDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.response.GoogleIdExistResponseDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.enums.GoogleIdExistence;
import com.mimaja.job_finder_app.security.authorization.googleAuth.service.GoogleAuthServiceDefault;
import com.mimaja.job_finder_app.security.shared.dto.TokenResponseDto;
import com.mimaja.job_finder_app.security.shared.utils.RegisterDataManager;
import com.mimaja.job_finder_app.security.smsCode.service.SmsCodeServiceDefault;
import com.mimaja.job_finder_app.security.token.refreshToken.service.RefreshTokenServiceDefault;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GoogleAuthServiceDefault - Unit Tests")
class GoogleAuthServiceDefaultTest {

    @InjectMocks
    private GoogleAuthServiceDefault googleAuthService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegisterDataManager registerDataManager;

    @Mock
    private RefreshTokenServiceDefault refreshTokenServiceDefault;

    @Mock
    private SmsCodeServiceDefault smsCodeServiceDefault;

    private GoogleIdTokenVerifier verifier;
    private GoogleIdToken googleIdToken;
    private GoogleIdToken.Payload payload;

    private User testUser;
    private TokenResponseDto testTokenResponse;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testTokenResponse = createTestTokenResponse();
        verifier = mock(GoogleIdTokenVerifier.class);
        googleIdToken = mock(GoogleIdToken.class);
        payload = mock(GoogleIdToken.Payload.class);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("user@example.com");
        user.setGoogleId("google_id_123");
        user.setPhoneNumber(123456789);
        return user;
    }

    private TokenResponseDto createTestTokenResponse() {
        return new TokenResponseDto(
            "access_token_value",
            "refresh_token_value",
            "refresh_token_id_value"
        );
    }

    private GoogleAuthLoginRequestDto createValidGoogleLoginRequest() {
        return new GoogleAuthLoginRequestDto("valid_google_token", 0);
    }

    private GoogleAuthLoginRequestDto createGoogleLoginRequestWithSmsCode(int smsCode) {
        return new GoogleAuthLoginRequestDto("valid_google_token", smsCode);
    }

    private GoogleAuthCheckExistenceRequestDto createValidGoogleCheckExistenceRequest() {
        return new GoogleAuthCheckExistenceRequestDto("valid_google_token");
    }

    private GoogleAuthRegisterRequestDto createValidGoogleRegisterRequest() {
        return new GoogleAuthRegisterRequestDto("valid_google_token", "newuser", 987654321);
    }

    private GoogleAuthRegisterRequestDto createGoogleRegisterRequestWithUsername(String username) {
        return new GoogleAuthRegisterRequestDto("valid_google_token", username, 987654321);
    }

    private GoogleAuthRegisterRequestDto createGoogleRegisterRequestWithPhoneNumber(int phoneNumber) {
        return new GoogleAuthRegisterRequestDto("valid_google_token", "newuser", phoneNumber);
    }

    private void setupSuccessfulTokenVerification(String googleId, String email) throws Exception {
        when(verifier.verify(anyString())).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getSubject()).thenReturn(googleId);
        when(payload.getEmail()).thenReturn(email);
        injectVerifier();
    }

    private void setupFailedTokenVerification() throws Exception {
        when(verifier.verify(anyString()))
                .thenThrow(new java.security.GeneralSecurityException("Invalid token"));
        injectVerifier();
    }

    private void setupNullTokenVerification() throws Exception {
        when(verifier.verify(anyString())).thenReturn(null);
        injectVerifier();
    }

    private void setupIOExceptionTokenVerification() throws Exception {
        when(verifier.verify(anyString()))
                .thenThrow(new java.io.IOException("Token verification failed"));
        injectVerifier();
    }

    private void setupNullPayloadTokenVerification() throws Exception {
        when(verifier.verify(anyString())).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(null);
        injectVerifier();
    }

    private void injectVerifier() throws Exception {
        java.lang.reflect.Field field = GoogleAuthServiceDefault.class.getDeclaredField("verifier");
        field.setAccessible(true);
        field.set(googleAuthService, verifier);
    }

    @Test
    @DisplayName("Should return token for valid Google login with existing Google ID")
    void testTryToLoginViaGoogle_WithExistingGoogleId_ShouldReturnToken() throws Exception {
        GoogleAuthLoginRequestDto loginRequest = createValidGoogleLoginRequest();
        setupSuccessfulTokenVerification("google_id_123", "user@example.com");
        when(userRepository.findByGoogleId("google_id_123")).thenReturn(Optional.of(testUser));
        when(refreshTokenServiceDefault.createTokensSet(testUser)).thenReturn(testTokenResponse);

        GoogleAuthLoginResponseDto result = googleAuthService.tryToLoginViaGoogle(loginRequest);

        assertNotNull(result, "Response should not be null");
        assertNotNull(result.tokens(), "Tokens should not be null");
        assertFalse(result.changedEmail(), "Email should not be changed");
        assertEquals("access_token_value", result.tokens().accessToken());
    }

    @Test
    @DisplayName("Should throw BusinessException for invalid Google token")
    void testTryToLoginViaGoogle_WithInvalidToken_ShouldThrowException() throws Exception {
        GoogleAuthLoginRequestDto loginRequest = createValidGoogleLoginRequest();
        setupFailedTokenVerification();

        assertThrows(
                BusinessException.class,
                () -> googleAuthService.tryToLoginViaGoogle(loginRequest),
                "Should throw BusinessException for invalid Google token");
    }

    @Test
    @DisplayName("Should throw BusinessException when token verification returns null")
    void testTryToLoginViaGoogle_WithNullVerification_ShouldThrowException() throws Exception {
        GoogleAuthLoginRequestDto loginRequest = createValidGoogleLoginRequest();
        setupNullTokenVerification();

        assertThrows(
                BusinessException.class,
                () -> googleAuthService.tryToLoginViaGoogle(loginRequest),
                "Should throw BusinessException when token verification returns null");
    }

    @Test
    @DisplayName("Should throw NullPointerException when token payload is null")
    void testTryToLoginViaGoogle_WithNullPayload_ShouldThrowException() throws Exception {
        GoogleAuthLoginRequestDto loginRequest = createValidGoogleLoginRequest();
        setupNullPayloadTokenVerification();

        assertThrows(
                NullPointerException.class,
                () -> googleAuthService.tryToLoginViaGoogle(loginRequest),
                "Should throw NullPointerException when token payload is null");
    }

    @Test
    @DisplayName("Should throw BusinessException when Google ID does not exist")
    void testTryToLoginViaGoogle_WithNonExistentGoogleId_ShouldThrowException() throws Exception {
        GoogleAuthLoginRequestDto loginRequest = createValidGoogleLoginRequest();
        setupSuccessfulTokenVerification("unknown_google_id", "unknown@example.com");
        when(userRepository.findByGoogleId("unknown_google_id")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> googleAuthService.tryToLoginViaGoogle(loginRequest),
                "Should throw BusinessException when Google ID does not exist");
    }

    @Test
    @DisplayName("Should link Google ID to existing user with SMS validation")
    void testTryToLoginViaGoogle_WithExistingEmailAndValidSms_ShouldLinkGoogleId() throws Exception {
        GoogleAuthLoginRequestDto loginRequest = createGoogleLoginRequestWithSmsCode(123456);
        User userWithoutGoogleId = createTestUser();
        userWithoutGoogleId.setGoogleId(null);

        setupSuccessfulTokenVerification("new_google_id_456", "user@example.com");
        when(userRepository.findByGoogleId("new_google_id_456")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userWithoutGoogleId));
        when(userRepository.save(any(User.class))).thenReturn(userWithoutGoogleId);
        doNothing().when(smsCodeServiceDefault).validateCode(any(UUID.class), anyInt());
        when(refreshTokenServiceDefault.createTokensSet(any(User.class))).thenReturn(testTokenResponse);

        GoogleAuthLoginResponseDto result = googleAuthService.tryToLoginViaGoogle(loginRequest);

        assertNotNull(result, "Response should not be null");
        verify(smsCodeServiceDefault).validateCode(any(UUID.class), anyInt());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when SMS code is missing for new user linkage")
    void testTryToLoginViaGoogle_WithMissingSmsCode_ShouldThrowException() throws Exception {
        GoogleAuthLoginRequestDto loginRequest = createGoogleLoginRequestWithSmsCode(0);
        User userWithoutGoogleId = createTestUser();
        userWithoutGoogleId.setGoogleId(null);

        setupSuccessfulTokenVerification("new_google_id_456", "user@example.com");
        when(userRepository.findByGoogleId("new_google_id_456")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userWithoutGoogleId));

        assertThrows(
                BusinessException.class,
                () -> googleAuthService.tryToLoginViaGoogle(loginRequest),
                "Should throw BusinessException when SMS code is missing");
    }

    @Test
    @DisplayName("Should return USER_EXIST when user has Google ID")
    void testCheckUserExistence_WithExistingGoogleId_ShouldReturnUserExist() throws Exception {
        GoogleAuthCheckExistenceRequestDto checkRequest = createValidGoogleCheckExistenceRequest();
        setupSuccessfulTokenVerification("google_id_123", "user@example.com");
        when(userRepository.findByGoogleId("google_id_123")).thenReturn(Optional.of(testUser));

        GoogleIdExistResponseDto result = googleAuthService.checkUserExistence(checkRequest);

        assertNotNull(result, "Response should not be null");
        assertEquals(GoogleIdExistence.USER_EXIST, result.exist());
    }

    @Test
    @DisplayName("Should return USER_EXIST_WITH_EMAIL when user has same email")
    void testCheckUserExistence_WithExistingEmail_ShouldReturnUserExistWithEmail() throws Exception {
        GoogleAuthCheckExistenceRequestDto checkRequest = createValidGoogleCheckExistenceRequest();
        setupSuccessfulTokenVerification("new_google_id", "user@example.com");
        when(userRepository.findByGoogleId("new_google_id")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(smsCodeServiceDefault).createCode(any(UUID.class));

        GoogleIdExistResponseDto result = googleAuthService.checkUserExistence(checkRequest);

        assertNotNull(result, "Response should not be null");
        assertEquals(GoogleIdExistence.USER_EXIST_WITH_EMAIL, result.exist());
        verify(smsCodeServiceDefault).createCode(any(UUID.class));
    }

    @Test
    @DisplayName("Should return USER_NOT_EXIST when user does not exist")
    void testCheckUserExistence_WithNonExistentUser_ShouldReturnUserNotExist() throws Exception {
        GoogleAuthCheckExistenceRequestDto checkRequest = createValidGoogleCheckExistenceRequest();
        setupSuccessfulTokenVerification("unknown_google_id", "unknown@example.com");
        when(userRepository.findByGoogleId("unknown_google_id")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        GoogleIdExistResponseDto result = googleAuthService.checkUserExistence(checkRequest);

        assertNotNull(result, "Response should not be null");
        assertEquals(GoogleIdExistence.USER_NOT_EXIST, result.exist());
    }

    @Test
    @DisplayName("Should throw BusinessException for invalid token during check existence")
    void testCheckUserExistence_WithInvalidToken_ShouldThrowException() throws Exception {
        GoogleAuthCheckExistenceRequestDto checkRequest = createValidGoogleCheckExistenceRequest();
        setupIOExceptionTokenVerification();

        assertThrows(
                BusinessException.class,
                () -> googleAuthService.checkUserExistence(checkRequest),
                "Should throw BusinessException for invalid token");
    }

    @Test
    @DisplayName("Should throw BusinessException when token verification returns null during check existence")
    void testCheckUserExistence_WithNullVerification_ShouldThrowException() throws Exception {
        GoogleAuthCheckExistenceRequestDto checkRequest = createValidGoogleCheckExistenceRequest();
        setupNullTokenVerification();

        assertThrows(
                BusinessException.class,
                () -> googleAuthService.checkUserExistence(checkRequest),
                "Should throw BusinessException when token verification returns null");
    }

    @Test
    @DisplayName("Should throw NullPointerException when token payload is null during check existence")
    void testCheckUserExistence_WithNullPayload_ShouldThrowException() throws Exception {
        GoogleAuthCheckExistenceRequestDto checkRequest = createValidGoogleCheckExistenceRequest();
        setupNullPayloadTokenVerification();

        assertThrows(
                NullPointerException.class,
                () -> googleAuthService.checkUserExistence(checkRequest),
                "Should throw NullPointerException when token payload is null");
    }

    @Test
    @DisplayName("Should return token for valid Google registration")
    void testTryToRegisterViaGoogle_WithValidCredentials_ShouldReturnToken() throws Exception {
        GoogleAuthRegisterRequestDto registerRequest = createValidGoogleRegisterRequest();
        setupSuccessfulTokenVerification("google_id_789", "newuser@example.com");
        doNothing().when(registerDataManager).checkRegisterDataGoogle("newuser", "newuser@example.com", 987654321, "google_id_789");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(refreshTokenServiceDefault.createTokensSet(any(User.class))).thenReturn(testTokenResponse);

        TokenResponseDto result = googleAuthService.tryToRegisterViaGoogle(registerRequest);

        assertNotNull(result, "Response should not be null");
        assertNotNull(result.accessToken(), "Access token should not be null");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessException for invalid token during registration")
    void testTryToRegisterViaGoogle_WithInvalidToken_ShouldThrowException() throws Exception {
        GoogleAuthRegisterRequestDto registerRequest = createValidGoogleRegisterRequest();
        setupFailedTokenVerification();

        assertThrows(
                BusinessException.class,
                () -> googleAuthService.tryToRegisterViaGoogle(registerRequest),
                "Should throw BusinessException for invalid token");
    }

    @Test
    @DisplayName("Should throw BusinessException when token verification returns null during registration")
    void testTryToRegisterViaGoogle_WithNullVerification_ShouldThrowException() throws Exception {
        GoogleAuthRegisterRequestDto registerRequest = createValidGoogleRegisterRequest();
        setupNullTokenVerification();

        assertThrows(
                BusinessException.class,
                () -> googleAuthService.tryToRegisterViaGoogle(registerRequest),
                "Should throw BusinessException when token verification returns null");
    }

    @Test
    @DisplayName("Should throw NullPointerException when token payload is null during registration")
    void testTryToRegisterViaGoogle_WithNullPayload_ShouldThrowException() throws Exception {
        GoogleAuthRegisterRequestDto registerRequest = createValidGoogleRegisterRequest();
        setupNullPayloadTokenVerification();

        assertThrows(
                NullPointerException.class,
                () -> googleAuthService.tryToRegisterViaGoogle(registerRequest),
                "Should throw NullPointerException when token payload is null");
    }

    @Test
    @DisplayName("Should throw BusinessException for empty username during registration")
    void testTryToRegisterViaGoogle_WithEmptyUsername_ShouldThrowException() throws Exception {
        GoogleAuthRegisterRequestDto registerRequest = createGoogleRegisterRequestWithUsername("");
        setupSuccessfulTokenVerification("google_id_789", "newuser@example.com");
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataGoogle("", "newuser@example.com", 987654321, "google_id_789");

        assertThrows(
                BusinessException.class,
                () -> googleAuthService.tryToRegisterViaGoogle(registerRequest),
                "Should throw BusinessException for empty username");
    }

    @Test
    @DisplayName("Should throw BusinessException for invalid phone number during registration")
    void testTryToRegisterViaGoogle_WithInvalidPhoneNumber_ShouldThrowException() throws Exception {
        GoogleAuthRegisterRequestDto registerRequest = createGoogleRegisterRequestWithPhoneNumber(0);
        setupSuccessfulTokenVerification("google_id_789", "newuser@example.com");
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataGoogle("newuser", "newuser@example.com", 0, "google_id_789");

        assertThrows(
                BusinessException.class,
                () -> googleAuthService.tryToRegisterViaGoogle(registerRequest),
                "Should throw BusinessException for invalid phone number");
    }

        @Test
    @DisplayName("Should return token and update email when user exists without email")
    void testTryToLoginViaGoogle_WithExistingUserWithoutEmail_ShouldUpdateEmail() throws Exception {
        GoogleAuthLoginRequestDto loginRequest = createValidGoogleLoginRequest();
        User userWithoutEmail = createTestUser();
        userWithoutEmail.setEmail(null);

        setupSuccessfulTokenVerification("google_id_123", "newemail@example.com");
        when(userRepository.findByGoogleId("google_id_123")).thenReturn(Optional.of(userWithoutEmail));
        when(userRepository.save(any(User.class))).thenReturn(userWithoutEmail);
        when(refreshTokenServiceDefault.createTokensSet(any(User.class))).thenReturn(testTokenResponse);

        GoogleAuthLoginResponseDto result = googleAuthService.tryToLoginViaGoogle(loginRequest);

        assertNotNull(result, "Response should not be null");
        assertNotNull(result.tokens(), "Tokens should not be null");
        assertEquals("newemail@example.com", userWithoutEmail.getEmail(), "Email should be updated from token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should update email and return true when email has changed")
    void testTryToLoginViaGoogle_WithChangedEmail_ShouldReturnChangedEmailTrue() throws Exception {
        GoogleAuthLoginRequestDto loginRequest = createValidGoogleLoginRequest();
        User userWithOldEmail = createTestUser();
        userWithOldEmail.setEmail("oldemail@example.com");

        setupSuccessfulTokenVerification("google_id_123", "newemail@example.com");
        when(userRepository.findByGoogleId("google_id_123")).thenReturn(Optional.of(userWithOldEmail));
        when(userRepository.save(any(User.class))).thenReturn(userWithOldEmail);
        when(refreshTokenServiceDefault.createTokensSet(any(User.class))).thenReturn(testTokenResponse);

        GoogleAuthLoginResponseDto result = googleAuthService.tryToLoginViaGoogle(loginRequest);

        assertNotNull(result, "Response should not be null");
        assertEquals(true, result.changedEmail(), "Email should be marked as changed");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should return registration token with proper structure")
    void testTryToRegisterViaGoogle_ShouldReturnProperTokenStructure() throws Exception {
        GoogleAuthRegisterRequestDto registerRequest = createValidGoogleRegisterRequest();
        setupSuccessfulTokenVerification("google_id_789", "newuser@example.com");
        doNothing().when(registerDataManager).checkRegisterDataGoogle("newuser", "newuser@example.com", 987654321, "google_id_789");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(refreshTokenServiceDefault.createTokensSet(any(User.class))).thenReturn(testTokenResponse);

        TokenResponseDto result = googleAuthService.tryToRegisterViaGoogle(registerRequest);

        assertThat(result)
                .as("Token response should not be null")
                .isNotNull();
        assertThat(result.accessToken())
                .as("Access token should be present and not empty")
                .isNotEmpty();
        assertThat(result.refreshToken())
                .as("Refresh token should be present and not empty")
                .isNotEmpty();
        assertThat(result.refreshTokenId())
                .as("Refresh token ID should be present and not empty")
                .isNotEmpty();
    }
}