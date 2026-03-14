package com.mimaja.job_finder_app.feature.unit.security.token.accessToken.service;

import com.auth0.jwt.JWT;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.profilephoto.dto.ProfilePhotoCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.profilephoto.model.ProfilePhoto;
import com.mimaja.job_finder_app.security.token.accessToken.dto.response.CreateAccessTokenResponseDto;
import com.mimaja.job_finder_app.security.token.accessToken.service.AccessTokenServiceDefault;
import com.mimaja.job_finder_app.security.token.accessToken.utils.AccessTokenSecretKeyManager;
import com.mimaja.job_finder_app.shared.enums.MimeType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessTokenServiceDefault - Unit Tests")
class AccessTokenServiceDefaultTest {

    @InjectMocks
    private AccessTokenServiceDefault accessTokenService;

    @Mock
    private AccessTokenSecretKeyManager accessTokenSecretKeyManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
    }

    private void setupSecretKeyMock() {
        when(accessTokenSecretKeyManager.getSecretKey()).thenReturn("test-secret-key-for-testing-purposes");
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
        return user;
    }

    private User createTestUserWithProfilePhoto() {
        ProfilePhotoCreateRequestDto profilePhotoDto =
          new ProfilePhotoCreateRequestDto("example",
          MimeType.PNG, 1, "example");
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("user@example.com");
        user.setPhoneNumber(123456789);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setProfileDescription("Test profile description");
        user.setProfilePhoto(ProfilePhoto.from(profilePhotoDto));
        return user;
    }

    @Test
    @DisplayName("Should create token for valid user")
    void testCreateToken_WithValidUser_ShouldReturnToken() {
        setupSecretKeyMock();
        CreateAccessTokenResponseDto result = accessTokenService.createToken(testUser);

        assertNotNull(result, "Response should not be null");
        assertNotNull(result.accessToken(), "Access token should not be null");
        assertThat(result.accessToken())
                .as("Access token should not be empty")
                .isNotBlank();
    }

    @Test
    @DisplayName("Should throw exception for null user")
    void testCreateToken_WithNullUser_ShouldThrowException() {
        assertThrows(
                NullPointerException.class,
                () -> accessTokenService.createToken(null),
                "Should throw NullPointerException for null user");
    }

    @Test
    @DisplayName("Should include all required claims in token")
    void testCreateToken_WithValidUser_ShouldIncludeAllClaims() {
        setupSecretKeyMock();
        CreateAccessTokenResponseDto result = accessTokenService.createToken(testUser);

        String token = result.accessToken();
        assertNotNull(token, "Token should not be null");

        var decodedJWT = JWT.decode(token);
        assertThat(decodedJWT.getSubject())
                .as("Subject should be user ID")
                .isEqualTo(testUser.getId().toString());
        assertThat(decodedJWT.getClaim("username").asString())
                .as("Username claim should match")
                .isEqualTo(testUser.getUsername());
        assertThat(decodedJWT.getClaim("email").asString())
                .as("Email claim should match")
                .isEqualTo(testUser.getEmail());
        assertThat(decodedJWT.getClaim("phoneNumber").asInt())
                .as("Phone number claim should match")
                .isEqualTo(testUser.getPhoneNumber());
        assertThat(decodedJWT.getClaim("firstName").asString())
                .as("First name claim should match")
                .isEqualTo(testUser.getFirstName());
        assertThat(decodedJWT.getClaim("lastName").asString())
                .as("Last name claim should match")
                .isEqualTo(testUser.getLastName());
        assertThat(decodedJWT.getClaim("profileDescription").asString())
                .as("Profile description claim should match")
                .isEqualTo(testUser.getProfileDescription());
    }

    @Test
    @DisplayName("Should include role claim in token")
    void testCreateToken_WithValidUser_ShouldIncludeRoleClaim() {
        setupSecretKeyMock();
        CreateAccessTokenResponseDto result = accessTokenService.createToken(testUser);

        String token = result.accessToken();
        var decodedJWT = JWT.decode(token);
        assertThat(decodedJWT.getClaim("role").asString())
                .as("Role claim should be present")
                .isNotNull();
    }

    @Test
    @DisplayName("Should handle user without profile photo")
    void testCreateToken_WithUserWithoutProfilePhoto_ShouldSetEmptyProfilePhoto() {
        setupSecretKeyMock();
        CreateAccessTokenResponseDto result = accessTokenService.createToken(testUser);

        String token = result.accessToken();
        var decodedJWT = JWT.decode(token);
        assertThat(decodedJWT.getClaim("profilePhoto").asString())
                .as("Profile photo should be empty when user has no profile photo")
                .isEmpty();
    }

    @Test
    @DisplayName("Should set issued at and expires at timestamps")
    void testCreateToken_WithValidUser_ShouldSetTimestamps() {
        setupSecretKeyMock();
        CreateAccessTokenResponseDto result = accessTokenService.createToken(testUser);

        String token = result.accessToken();
        var decodedJWT = JWT.decode(token);
        assertNotNull(decodedJWT.getIssuedAt(), "Issued at should not be null");
        assertNotNull(decodedJWT.getExpiresAt(), "Expires at should not be null");
        assertThat(decodedJWT.getExpiresAt())
                .as("Token should expire after issued at")
                .isAfter(decodedJWT.getIssuedAt());
    }

    @Test
    @DisplayName("Should create token with 5 minute lifetime")
    void testCreateToken_WithValidUser_ShouldHaveFiveMinuteLifetime() {
        setupSecretKeyMock();
        CreateAccessTokenResponseDto result = accessTokenService.createToken(testUser);

        String token = result.accessToken();
        var decodedJWT = JWT.decode(token);
        long issuedAtTime = decodedJWT.getIssuedAt().getTime();
        long expiresAtTime = decodedJWT.getExpiresAt().getTime();
        long lifetimeMs = expiresAtTime - issuedAtTime;
        long expectedLifetimeMs = 5 * 60 * 1000;

        assertThat(lifetimeMs)
                .as("Token lifetime should be approximately 5 minutes")
                .isCloseTo(expectedLifetimeMs, org.assertj.core.api.Assertions.within(1000L));
    }

    @Test
    @DisplayName("Should return token with proper structure")
    void testCreateToken_WithValidUser_ShouldReturnProperTokenStructure() {
        setupSecretKeyMock();
        CreateAccessTokenResponseDto result = accessTokenService.createToken(testUser);

        assertThat(result)
                .as("Token response should not be null")
                .isNotNull();
        assertThat(result.accessToken())
                .as("Access token should be present and not empty")
                .isNotEmpty()
                .contains(".");
    }

    @Test
    @DisplayName("Should handle user with all fields populated")
    void testCreateToken_WithUserWithAllFieldsPopulated_ShouldIncludeAllClaims() {
        setupSecretKeyMock();
        User userWithAllFields = createTestUserWithProfilePhoto();

        CreateAccessTokenResponseDto result = accessTokenService.createToken(userWithAllFields);

        assertNotNull(result, "Response should not be null");
        assertNotNull(result.accessToken(), "Access token should not be null");
        assertThat(result.accessToken()).isNotBlank();
    }

    @Test
    @DisplayName("Should handle user with all fields populated except profile photo")
    void testCreateToken_WithUserWithAllFieldsPopulated_ShouldIncludeAllClaimsExceptProfilePhoto() {
        setupSecretKeyMock();
        User userWithAllFields = createTestUser();

        CreateAccessTokenResponseDto result = accessTokenService.createToken(userWithAllFields);

        assertNotNull(result, "Response should not be null");
        assertNotNull(result.accessToken(), "Access token should not be null");
        assertThat(result.accessToken()).isNotBlank();
    }
}
