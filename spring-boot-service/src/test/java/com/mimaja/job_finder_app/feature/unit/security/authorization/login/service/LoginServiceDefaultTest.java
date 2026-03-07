package com.mimaja.job_finder_app.feature.unit.security.authorization.login.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.security.authorization.login.dto.request.LoginRequestDto;
import com.mimaja.job_finder_app.security.authorization.login.service.LoginServiceDefault;
import com.mimaja.job_finder_app.security.authorization.login.utils.DefaultLoginValidation;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.shared.dto.TokenResponseDto;
import com.mimaja.job_finder_app.security.token.refreshToken.service.RefreshTokenServiceDefault;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginServiceDefault - Unit Tests")
class LoginServiceDefaultTest {

    @InjectMocks
    private LoginServiceDefault loginService;

    @Mock
    private DefaultLoginValidation defaultLoginValidation;

    @Mock
    private PasswordConfiguration passwordConfiguration;

    @Mock
    private RefreshTokenServiceDefault refreshTokenServiceDefault;

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
        user.setEmail("user@example.com");
        user.setPasswordHash("$2a$10$hashedpassword123");
        return user;
    }

    private TokenResponseDto createTestTokenResponse() {
        return new TokenResponseDto(
            "access_token_value",
            "refresh_token_value",
            "refresh_token_id_value"
        );
    }

    private LoginRequestDto createValidLoginRequest() {
        return new LoginRequestDto("user@example.com", "password123");
    }

    private LoginRequestDto createLoginRequestWithLoginData(String loginData) {
        return new LoginRequestDto(loginData, "password123");
    }

    private LoginRequestDto createLoginRequestWithPassword(String password) {
        return new LoginRequestDto("user@example.com", password);
    }

    @Test
    @DisplayName("Should return token for valid login credentials")
    void testTryToLogin_WithValidCredentials_ShouldReturnToken() {
        LoginRequestDto validRequest = createValidLoginRequest();
        when(defaultLoginValidation.userValidation("user@example.com")).thenReturn(testUser);
        when(passwordConfiguration.verifyPassword("password123", testUser.getPasswordHash())).thenReturn(true);
        when(refreshTokenServiceDefault.createTokensSet(testUser)).thenReturn(testTokenResponse);

        TokenResponseDto result = loginService.tryToLogin(validRequest);

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
    }

    @Test
    @DisplayName("Should throw exception for null request")
    void testTryToLogin_WithNullRequest_ShouldThrowException() {
        assertThrows(
                NullPointerException.class,
                () -> loginService.tryToLogin(null),
                "Should throw NullPointerException for null request");
    }

    @Test
    @DisplayName("Should throw BusinessException for empty login data")
    void testTryToLogin_WithEmptyLoginData_ShouldThrowException() {
        LoginRequestDto invalidRequest = createLoginRequestWithLoginData("");
        when(defaultLoginValidation.userValidation("")).thenThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA));

        assertThrows(
                BusinessException.class,
                () -> loginService.tryToLogin(invalidRequest),
                "Should throw BusinessException for empty login data");
    }

    @Test
    @DisplayName("Should throw BusinessException for empty password")
    void testTryToLogin_WithEmptyPassword_ShouldThrowException() {
        LoginRequestDto invalidRequest = createLoginRequestWithPassword("");
        when(defaultLoginValidation.userValidation("user@example.com")).thenReturn(testUser);
        when(passwordConfiguration.verifyPassword("", testUser.getPasswordHash())).thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> loginService.tryToLogin(invalidRequest),
                "Should throw BusinessException for invalid password");
    }

    @Test
    @DisplayName("Should throw BusinessException for null login data")
    void testTryToLogin_WithNullLoginData_ShouldThrowException() {
        LoginRequestDto invalidRequest = createLoginRequestWithLoginData(null);
        when(defaultLoginValidation.userValidation(null)).thenThrow(new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA));

        assertThrows(
                BusinessException.class,
                () -> loginService.tryToLogin(invalidRequest),
                "Should throw BusinessException for null login data");
    }

    @Test
    @DisplayName("Should throw BusinessException for null password")
    void testTryToLogin_WithNullPassword_ShouldThrowException() {
        LoginRequestDto invalidRequest = createLoginRequestWithPassword(null);
        when(defaultLoginValidation.userValidation("user@example.com")).thenReturn(testUser);
        when(passwordConfiguration.verifyPassword(null, testUser.getPasswordHash())).thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> loginService.tryToLogin(invalidRequest),
                "Should throw BusinessException for null password");
    }

    @Test
    @DisplayName("Should throw BusinessException for wrong password")
    void testTryToLogin_WithWrongPassword_ShouldThrowException() {
        LoginRequestDto invalidRequest = createLoginRequestWithPassword("wrongpassword");
        when(defaultLoginValidation.userValidation("user@example.com")).thenReturn(testUser);
        when(passwordConfiguration.verifyPassword("wrongpassword", testUser.getPasswordHash())).thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> loginService.tryToLogin(invalidRequest),
                "Should throw BusinessException for wrong password");
    }

    @Test
    @DisplayName("Should throw BusinessException when user has no password")
    void testTryToLogin_WithNullPasswordHash_ShouldThrowException() {
        LoginRequestDto validRequest = createValidLoginRequest();
        User userWithoutPassword = createTestUser();
        userWithoutPassword.setPasswordHash(null);

        when(defaultLoginValidation.userValidation("user@example.com")).thenReturn(userWithoutPassword);

        assertThrows(
                BusinessException.class,
                () -> loginService.tryToLogin(validRequest),
                "Should throw BusinessException when user has no password");
    }

    @Test
    @DisplayName("Should return token with proper structure")
    void testTryToLogin_WithValidCredentials_ShouldReturnProperTokenStructure() {
        LoginRequestDto validRequest = createValidLoginRequest();
        when(defaultLoginValidation.userValidation("user@example.com")).thenReturn(testUser);
        when(passwordConfiguration.verifyPassword("password123", testUser.getPasswordHash())).thenReturn(true);
        when(refreshTokenServiceDefault.createTokensSet(testUser)).thenReturn(testTokenResponse);

        TokenResponseDto result = loginService.tryToLogin(validRequest);

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