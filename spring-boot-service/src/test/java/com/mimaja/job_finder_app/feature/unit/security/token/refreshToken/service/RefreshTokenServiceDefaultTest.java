package com.mimaja.job_finder_app.feature.unit.security.token.refreshToken.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.security.encoder.RefreshTokenEncoder;
import com.mimaja.job_finder_app.security.token.accessToken.dto.response.CreateAccessTokenResponseDto;
import com.mimaja.job_finder_app.security.token.accessToken.service.AccessTokenService;
import com.mimaja.job_finder_app.security.token.refreshToken.dto.request.RequestRefreshTokenRotateDto;
import com.mimaja.job_finder_app.security.token.refreshToken.dto.response.RefreshTokenResponseDto;
import com.mimaja.job_finder_app.security.shared.dto.TokenResponseDto;
import com.mimaja.job_finder_app.security.token.refreshToken.service.RefreshTokenServiceDefault;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenServiceDefault - Unit Tests")
public class RefreshTokenServiceDefaultTest {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOps;

    @Mock
    private SetOperations<String, String> setOps;

    @Mock
    private RefreshTokenEncoder refreshTokenEncoder;

    @Mock
    private AccessTokenService accessTokenService;

    @Mock
    private UserRepository userRepository;

    private RefreshTokenServiceDefault refreshTokenService;

    private User testUser;

    void setUp() {
        testUser = createTestUser();

        refreshTokenService = new RefreshTokenServiceDefault(
            redisTemplate,
            refreshTokenEncoder,
            accessTokenService,
            userRepository
        );
    }

    void setUpOneToken() {
        setUpForCreatingValidToken();
        refreshTokenService.createRefreshToken(testUser);
    }

    void setUpReturnSetOpsMock() {
        setUp();
        when(redisTemplate.opsForSet()).thenReturn(setOps);
    }

    private void setUpForCreatingValidToken() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(refreshTokenEncoder.encodeToken(anyString()))
            .thenReturn("encoded-test-token");

        setUp();
    }

    private void setUpOpsForHashToken() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);

        setUp();
    }

    // private void setUpValidRefreshToken() {
    //     when(refreshTokenEncoder.verifyToken(anyString(), anyString()))
    //         .thenReturn(true);
    // }

    // private void setUpInvalidRefreshToken() {
    //     when(refreshTokenEncoder.verifyToken(anyString(), anyString()))
    //         .thenReturn(false);
    // }

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

    @Test
    @DisplayName("Should create token for valid user")
    void testCreateToken_WithValidUser_ShouldReturnToken() {
        setUpForCreatingValidToken();
        RefreshTokenResponseDto result = refreshTokenService.createRefreshToken(testUser);

        assertNotNull(result, "Response should not be null");
        assertNotNull(result.refreshToken(), "Refresh token should not be null");
        assertThat(result.refreshToken())
            .as("Refresh token should not be empty")
            .isNotBlank();

        assertNotNull(result.refreshTokenId(), "Refresh token id should not be null");
        assertThat(result.refreshTokenId())
            .as("Refresh token id should not be empty")
            .isNotBlank();
    }

    @Test
    @DisplayName("Should delete all user tokens - no tokens")
    void testDeleteAllTokensNoTokens_WithValidUser_ShouldReturnNull() {
        setUpReturnSetOpsMock();

        UUID userId = testUser.getId();
        refreshTokenService.deleteAllUserTokens(userId);

        String expectedKey = "userTokens:" + userId;
        verify(setOps, times(1)).members(expectedKey);
    }

    @Test
    @DisplayName("Should delete all user tokens")
    void testDeleteAllTokens_WithValidUser_ShouldDeleteTokens() {
        setUpReturnSetOpsMock();

        UUID userId = testUser.getId();
        Set<String> tokenIds = Set.of("token-1", "token-2");

        when(setOps.members("userTokens:" + userId)).thenReturn(tokenIds);

        refreshTokenService.deleteAllUserTokens(userId);

        String expectedKey = "userTokens:" + userId;
        verify(setOps, times(1)).members(expectedKey);
        verify(redisTemplate, times(1)).delete(expectedKey);
    }

    @Test
    @DisplayName("Should delete single token by token id")
    void testDeleteToken_WithValidTokenId_ShouldDeleteToken() {
        setUp();

        String tokenId = UUID.randomUUID().toString();
        refreshTokenService.deleteToken(tokenId);

        String expectedKey = "RefreshToken-" + tokenId;
        verify(redisTemplate, times(1)).delete(expectedKey);
    }

    @Test
    @DisplayName("Should create tokens set for valid user")
    void testCreateTokensSet_WithValidUser_ShouldReturnTokenResponseDto() {
        setUpForCreatingValidToken();

        CreateAccessTokenResponseDto accessTokenDto = new CreateAccessTokenResponseDto("test-access-token");

        when(accessTokenService.createToken(testUser)).thenReturn(accessTokenDto);

        TokenResponseDto result = refreshTokenService.createTokensSet(testUser);

        assertNotNull(result, "TokenResponseDto should not be null");
        assertThat(result.accessToken())
            .as("Access token should match")
            .isEqualTo("test-access-token");
        assertThat(result.refreshToken())
            .as("Refresh token should not be empty")
            .isNotBlank();
        assertThat(result.refreshTokenId())
            .as("Refresh token ID should not be empty")
            .isNotBlank();

        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should rotate token successfully with valid refresh token")
    void testRotateToken_WithValidRefreshToken_ShouldReturnNewTokens() {
        setUpForCreatingValidToken();

        String refreshTokenId = UUID.randomUUID().toString();
        String hashedToken = "hashed-token-value";
        String refreshToken = "valid-refresh-token";

        RequestRefreshTokenRotateDto reqData = new RequestRefreshTokenRotateDto(refreshToken, refreshTokenId);

        when(hashOps.entries("RefreshToken-" + refreshTokenId))
            .thenReturn(Map.of(
                "userId", testUser.getId().toString(),
                "tokenValue", hashedToken
            ));

        when(refreshTokenEncoder.verifyToken(refreshToken, hashedToken)).thenReturn(true);

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        CreateAccessTokenResponseDto accessTokenDto = new CreateAccessTokenResponseDto("new-access-token");
        when(accessTokenService.createToken(testUser)).thenReturn(accessTokenDto);

        TokenResponseDto result = refreshTokenService.rotateToken(reqData);

        assertNotNull(result, "TokenResponseDto should not be null");
        assertThat(result.accessToken())
            .as("New access token should be returned")
            .isEqualTo("new-access-token");
        assertThat(result.refreshToken())
            .as("New refresh token should not be empty")
            .isNotBlank();
        assertThat(result.refreshTokenId())
            .as("New refresh token ID should not be empty")
            .isNotBlank();

        verify(redisTemplate, times(1)).delete("RefreshToken-" + refreshTokenId);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    @DisplayName("Should throw BusinessException when refresh token is invalid")
    void testRotateToken_WithInvalidRefreshToken_ShouldThrowBusinessException() {
        setUpOpsForHashToken();

        String refreshTokenId = UUID.randomUUID().toString();
        String hashedToken = "hashed-token-value";
        String invalidRefreshToken = "invalid-refresh-token";

        RequestRefreshTokenRotateDto reqData = new RequestRefreshTokenRotateDto(invalidRefreshToken, refreshTokenId);

        when(hashOps.entries("RefreshToken-" + refreshTokenId))
            .thenReturn(Map.of(
                "userId", testUser.getId().toString(),
                "tokenValue", hashedToken
            ));

        when(refreshTokenEncoder.verifyToken(invalidRefreshToken, hashedToken)).thenReturn(false);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> refreshTokenService.rotateToken(reqData),
            "Should throw BusinessException for invalid refresh token"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate invalid refresh token")
            .isEqualTo(BusinessExceptionReason.INVALID_REFRESH_TOKEN.getCode());
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not found")
    void testRotateToken_WithNonExistentUser_ShouldThrowBusinessException() {
        setUpOpsForHashToken();

        String refreshTokenId = UUID.randomUUID().toString();
        String hashedToken = "hashed-token-value";
        String refreshToken = "valid-refresh-token";
        UUID nonExistentUserId = UUID.randomUUID();

        RequestRefreshTokenRotateDto reqData = new RequestRefreshTokenRotateDto(refreshToken, refreshTokenId);

        when(hashOps.entries("RefreshToken-" + refreshTokenId))
            .thenReturn(Map.of(
                "userId", nonExistentUserId.toString(),
                "tokenValue", hashedToken
            ));

        when(refreshTokenEncoder.verifyToken(refreshToken, hashedToken)).thenReturn(true);

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> refreshTokenService.rotateToken(reqData),
            "Should throw BusinessException when user is not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate invalid refresh token")
            .isEqualTo(BusinessExceptionReason.INVALID_REFRESH_TOKEN.getCode());
    }
}
