package com.mimaja.job_finder_app.feature.unit.security.authorization.register.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.authorization.register.dto.request.RegisterRequestDto;
import com.mimaja.job_finder_app.security.authorization.register.service.RegisterServiceDefault;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.shared.dto.TokenResponseDto;
import com.mimaja.job_finder_app.security.shared.utils.RegisterDataManager;
import com.mimaja.job_finder_app.security.token.refreshToken.service.RefreshTokenServiceDefault;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterServiceDefault - Unit Tests")
class RegisterServiceDefaultTest {

    @InjectMocks
    private RegisterServiceDefault registerService;

    @Mock
    private PasswordConfiguration passwordConfiguration;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenServiceDefault refreshTokenServiceDefault;

    @Mock
    private RegisterDataManager registerDataManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private TokenResponseDto testTokenResponse;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testTokenResponse = createTestTokenResponse();
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("user@example.com");
        user.setPasswordHash("$2a$10$hashedpassword123");
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

    private RegisterRequestDto createValidRegisterRequest() {
        return new RegisterRequestDto("testuser", "user@example.com", 123456789, "password123");
    }

    private RegisterRequestDto createRegisterRequestWithUsername(String username) {
        return new RegisterRequestDto(username, "user@example.com", 123456789, "password123");
    }

    private RegisterRequestDto createRegisterRequestWithEmail(String email) {
        return new RegisterRequestDto("testuser", email, 123456789, "password123");
    }

    private RegisterRequestDto createRegisterRequestWithPassword(String password) {
        return new RegisterRequestDto("testuser", "user@example.com", 123456789, password);
    }

    private RegisterRequestDto createRegisterRequestWithPhoneNumber(int phoneNumber) {
        return new RegisterRequestDto("testuser", "user@example.com", phoneNumber, "password123");
    }

    @Test
    @DisplayName("Should return token for valid register credentials")
    void testTryToRegister_WithValidCredentials_ShouldReturnToken() {
        RegisterRequestDto validRequest = createValidRegisterRequest();
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedpassword123");
        when(passwordConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        doNothing().when(registerDataManager).checkRegisterDataDefault("testuser", "user@example.com", 123456789, "password123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(refreshTokenServiceDefault.createTokensSet(any(User.class))).thenReturn(testTokenResponse);

        TokenResponseDto result = registerService.tryToRegister(validRequest);

        assertNotNull(result, "Response should not be null");
        assertNotNull(result.accessToken(), "Access token should not be null");
        assertFalse(result.accessToken().isEmpty(), "Access token should not be empty");
        assertThat(result.accessToken())
                .as("Access token should have valid format")
                .isNotBlank();
        assertNotNull(result.refreshToken(), "Refresh token should not be null");
        assertFalse(result.refreshToken().isEmpty(), "Refresh token should not be empty");
        assertThat(result.refreshToken())
                .as("Refresh token should have valid format")
                .isNotBlank();
        assertNotNull(result.refreshTokenId(), "Refresh token ID should not be null");
        assertFalse(result.refreshTokenId().isEmpty(), "Refresh token ID should not be empty");
        assertThat(result.refreshTokenId())
                .as("Refresh token ID should have valid format")
                .isNotBlank();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception for null request")
    void testTryToRegister_WithNullRequest_ShouldThrowException() {
        assertThrows(
                NullPointerException.class,
                () -> registerService.tryToRegister(null),
                "Should throw NullPointerException for null request");
    }

    @Test
    @DisplayName("Should throw BusinessException for empty username")
    void testTryToRegister_WithEmptyUsername_ShouldThrowException() {
        RegisterRequestDto invalidRequest = createRegisterRequestWithUsername("");
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataDefault("", "user@example.com", 123456789, "password123");

        assertThrows(
                BusinessException.class,
                () -> registerService.tryToRegister(invalidRequest),
                "Should throw BusinessException for empty username");
    }

    @Test
    @DisplayName("Should throw BusinessException for empty email")
    void testTryToRegister_WithEmptyEmail_ShouldThrowException() {
        RegisterRequestDto invalidRequest = createRegisterRequestWithEmail("");
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataDefault("testuser", "", 123456789, "password123");

        assertThrows(
                BusinessException.class,
                () -> registerService.tryToRegister(invalidRequest),
                "Should throw BusinessException for empty email");
    }

    @Test
    @DisplayName("Should throw BusinessException for empty password")
    void testTryToRegister_WithEmptyPassword_ShouldThrowException() {
        RegisterRequestDto invalidRequest = createRegisterRequestWithPassword("");
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataDefault("testuser", "user@example.com", 123456789, "");

        assertThrows(
                BusinessException.class,
                () -> registerService.tryToRegister(invalidRequest),
                "Should throw BusinessException for empty password");
    }

    @Test
    @DisplayName("Should throw BusinessException for null username")
    void testTryToRegister_WithNullUsername_ShouldThrowException() {
        RegisterRequestDto invalidRequest = createRegisterRequestWithUsername(null);
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataDefault(null, "user@example.com", 123456789, "password123");

        assertThrows(
                BusinessException.class,
                () -> registerService.tryToRegister(invalidRequest),
                "Should throw BusinessException for null username");
    }

    @Test
    @DisplayName("Should throw BusinessException for null email")
    void testTryToRegister_WithNullEmail_ShouldThrowException() {
        RegisterRequestDto invalidRequest = createRegisterRequestWithEmail(null);
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataDefault("testuser", null, 123456789, "password123");

        assertThrows(
                BusinessException.class,
                () -> registerService.tryToRegister(invalidRequest),
                "Should throw BusinessException for null email");
    }

    @Test
    @DisplayName("Should throw BusinessException for null password")
    void testTryToRegister_WithNullPassword_ShouldThrowException() {
        RegisterRequestDto invalidRequest = createRegisterRequestWithPassword(null);
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataDefault("testuser", "user@example.com", 123456789, null);

        assertThrows(
                BusinessException.class,
                () -> registerService.tryToRegister(invalidRequest),
                "Should throw BusinessException for null password");
    }

    @Test
    @DisplayName("Should throw BusinessException for invalid email format")
    void testTryToRegister_WithInvalidEmailFormat_ShouldThrowException() {
        RegisterRequestDto invalidRequest = createRegisterRequestWithEmail("invalid-email");
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataDefault("testuser", "invalid-email", 123456789, "password123");

        assertThrows(
                BusinessException.class,
                () -> registerService.tryToRegister(invalidRequest),
                "Should throw BusinessException for invalid email format");
    }

    @Test
    @DisplayName("Should throw BusinessException for too short password")
    void testTryToRegister_WithTooShortPassword_ShouldThrowException() {
        RegisterRequestDto invalidRequest = createRegisterRequestWithPassword("123");
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataDefault("testuser", "user@example.com", 123456789, "123");

        assertThrows(
                BusinessException.class,
                () -> registerService.tryToRegister(invalidRequest),
                "Should throw BusinessException for too short password");
    }

    @Test
    @DisplayName("Should throw BusinessException for invalid phone number")
    void testTryToRegister_WithInvalidPhoneNumber_ShouldThrowException() {
        RegisterRequestDto invalidRequest = createRegisterRequestWithPhoneNumber(0);
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataDefault("testuser", "user@example.com", 0, "password123");

        assertThrows(
                BusinessException.class,
                () -> registerService.tryToRegister(invalidRequest),
                "Should throw BusinessException for invalid phone number");
    }

    @Test
    @DisplayName("Should return token with proper structure")
    void testTryToRegister_WithValidCredentials_ShouldReturnProperTokenStructure() {
        RegisterRequestDto validRequest = createValidRegisterRequest();
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedpassword123");
        when(passwordConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        doNothing().when(registerDataManager).checkRegisterDataDefault("testuser", "user@example.com", 123456789, "password123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(refreshTokenServiceDefault.createTokensSet(any(User.class))).thenReturn(testTokenResponse);

        TokenResponseDto result = registerService.tryToRegister(validRequest);

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

    @Test
    @DisplayName("Should hash password before saving user")
    void testTryToRegister_ShouldHashPasswordBeforeSavingUser() {
        RegisterRequestDto validRequest = createValidRegisterRequest();
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedpassword123");
        when(passwordConfiguration.passwordEncoder()).thenReturn(passwordEncoder);
        doNothing().when(registerDataManager).checkRegisterDataDefault("testuser", "user@example.com", 123456789, "password123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(refreshTokenServiceDefault.createTokensSet(any(User.class))).thenReturn(testTokenResponse);

        registerService.tryToRegister(validRequest);

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessException for duplicate email")
    void testTryToRegister_WithDuplicateEmail_ShouldThrowException() {
        RegisterRequestDto invalidRequest = createValidRegisterRequest();
        doThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA))
                .when(registerDataManager).checkRegisterDataDefault("testuser", "user@example.com", 123456789, "password123");

        assertThrows(
                BusinessException.class,
                () -> registerService.tryToRegister(invalidRequest),
                "Should throw BusinessException for duplicate email");
    }
}