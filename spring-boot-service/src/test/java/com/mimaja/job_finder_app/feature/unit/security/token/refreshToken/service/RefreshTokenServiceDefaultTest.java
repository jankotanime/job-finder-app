package com.mimaja.job_finder_app.feature.unit.security.token.refreshToken.service;

import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.encoder.RefreshTokenEncoder;
import com.mimaja.job_finder_app.security.token.accessToken.dto.response.CreateAccessTokenResponseDto;
import com.mimaja.job_finder_app.security.token.accessToken.service.AccessTokenService;
import com.mimaja.job_finder_app.security.token.refreshToken.dto.request.RequestRefreshTokenRotateDto;
import com.mimaja.job_finder_app.security.token.refreshToken.dto.response.RefreshTokenResponseDto;
import com.mimaja.job_finder_app.security.token.refreshToken.service.RefreshTokenServiceDefault;
import com.mimaja.job_finder_app.security.shared.dto.TokenResponseDto;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceDefaultTest {

    private static final String TEST_HASHED_TOKEN  = "hashed-token-value";
    private static final String TEST_ACCESS_TOKEN  = "test-access-token";
    private static final String TEST_ENCODED_TOKEN = "encoded-test-token";

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private HashOperations<String, String, String> hashOps;
    @Mock private SetOperations<String, String> setOps;
    @Mock private RefreshTokenEncoder refreshTokenEncoder;
    @Mock private AccessTokenService accessTokenService;
    @Mock private UserRepository userRepository;

    private RefreshTokenServiceDefault refreshTokenService;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        doReturn(hashOps).when(redisTemplate).opsForHash();
        refreshTokenService = new RefreshTokenServiceDefault(
                redisTemplate, refreshTokenEncoder, accessTokenService, userRepository);
    }

    @Test
    void createRefreshToken_shouldReturnNonBlankRefreshToken_whenUserIsValid() {
        when(refreshTokenEncoder.encodeToken(anyString())).thenReturn(TEST_ENCODED_TOKEN);
        RefreshTokenResponseDto result = refreshTokenService.createRefreshToken(testUser);
        assertThat(result.refreshToken()).isNotBlank();
    }

    @Test
    void createRefreshToken_shouldReturnNonBlankRefreshTokenId_whenUserIsValid() {
        when(refreshTokenEncoder.encodeToken(anyString())).thenReturn(TEST_ENCODED_TOKEN);
        RefreshTokenResponseDto result = refreshTokenService.createRefreshToken(testUser);
        assertThat(result.refreshTokenId()).isNotBlank();
    }

    @Test
    void deleteAllUserTokens_shouldCallMembersOnUserTokensKey_whenDeletingAllTokens() {
        when(redisTemplate.opsForSet()).thenReturn(setOps);
        UUID userId = testUser.getId();
        when(setOps.members("userTokens:" + userId)).thenReturn(Set.of());
        refreshTokenService.deleteAllUserTokens(userId);
        verify(setOps, times(1)).members("userTokens:" + userId);
    }

    @Test
    void deleteAllUserTokens_shouldDeleteTokensKey_whenUserHasTokens() {
        when(redisTemplate.opsForSet()).thenReturn(setOps);
        UUID userId = testUser.getId();
        when(setOps.members("userTokens:" + userId)).thenReturn(Set.of("token-1", "token-2"));
        refreshTokenService.deleteAllUserTokens(userId);
        verify(redisTemplate, times(1)).delete("userTokens:" + userId);
    }

    @Test
    void deleteToken_shouldDeleteRefreshTokenKey_whenDeletingToken() {
        String tokenId = UUID.randomUUID().toString();
        refreshTokenService.deleteToken(tokenId);
        verify(redisTemplate, times(1)).delete("RefreshToken-" + tokenId);
    }

    @Test
    void createTokensSet_shouldReturnCorrectAccessToken_whenUserIsValid() {
        when(refreshTokenEncoder.encodeToken(anyString())).thenReturn(TEST_ENCODED_TOKEN);
        when(accessTokenService.createToken(testUser))
                .thenReturn(new CreateAccessTokenResponseDto(TEST_ACCESS_TOKEN));
        TokenResponseDto result = refreshTokenService.createTokensSet(testUser);
        assertThat(result.accessToken()).isEqualTo(TEST_ACCESS_TOKEN);
    }

    @Test
    void createTokensSet_shouldReturnNonBlankRefreshToken_whenUserIsValid() {
        when(refreshTokenEncoder.encodeToken(anyString())).thenReturn(TEST_ENCODED_TOKEN);
        when(accessTokenService.createToken(testUser))
                .thenReturn(new CreateAccessTokenResponseDto(TEST_ACCESS_TOKEN));
        TokenResponseDto result = refreshTokenService.createTokensSet(testUser);
        assertThat(result.refreshToken()).isNotBlank();
    }

    @Test
    void createTokensSet_shouldReturnNonBlankRefreshTokenId_whenUserIsValid() {
        when(refreshTokenEncoder.encodeToken(anyString())).thenReturn(TEST_ENCODED_TOKEN);
        when(accessTokenService.createToken(testUser))
                .thenReturn(new CreateAccessTokenResponseDto(TEST_ACCESS_TOKEN));
        TokenResponseDto result = refreshTokenService.createTokensSet(testUser);
        assertThat(result.refreshTokenId()).isNotBlank();
    }

    @Test
    void createTokensSet_shouldCallCreateToken_whenCreatingTokenSet() {
        when(refreshTokenEncoder.encodeToken(anyString())).thenReturn(TEST_ENCODED_TOKEN);
        when(accessTokenService.createToken(testUser))
                .thenReturn(new CreateAccessTokenResponseDto(TEST_ACCESS_TOKEN));
        refreshTokenService.createTokensSet(testUser);
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    void rotateToken_shouldReturnNewAccessToken_whenRefreshTokenIsValid() {
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshToken = "valid-refresh-token";
        setupValidRotateTokenMocks(refreshTokenId, refreshToken);
        TokenResponseDto result = refreshTokenService.rotateToken(
                new RequestRefreshTokenRotateDto(refreshToken, refreshTokenId));
        assertThat(result.accessToken()).isEqualTo("new-access-token");
    }

    @Test
    void rotateToken_shouldReturnNonBlankNewRefreshToken_whenRefreshTokenIsValid() {
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshToken = "valid-refresh-token";
        setupValidRotateTokenMocks(refreshTokenId, refreshToken);
        TokenResponseDto result = refreshTokenService.rotateToken(
                new RequestRefreshTokenRotateDto(refreshToken, refreshTokenId));
        assertThat(result.refreshToken()).isNotBlank();
    }

    @Test
    void rotateToken_shouldDeleteOldRefreshToken_whenRotatingToken() {
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshToken = "valid-refresh-token";
        setupValidRotateTokenMocks(refreshTokenId, refreshToken);
        refreshTokenService.rotateToken(new RequestRefreshTokenRotateDto(refreshToken, refreshTokenId));
        verify(redisTemplate, times(1)).delete("RefreshToken-" + refreshTokenId);
    }

    @Test
    void rotateToken_shouldCallCreateToken_whenRotatingToken() {
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshToken = "valid-refresh-token";
        setupValidRotateTokenMocks(refreshTokenId, refreshToken);
        refreshTokenService.rotateToken(new RequestRefreshTokenRotateDto(refreshToken, refreshTokenId));
        verify(accessTokenService, times(1)).createToken(testUser);
    }

    @Test
    void rotateToken_shouldThrowExceptionWithInvalidRefreshTokenCode_whenTokenIsInvalid() {
        String refreshTokenId = UUID.randomUUID().toString();
        String invalidToken = "invalid-refresh-token";
        when(hashOps.entries("RefreshToken-" + refreshTokenId))
                .thenReturn(Map.of("userId", testUser.getId().toString(), "tokenValue", TEST_HASHED_TOKEN));
        when(refreshTokenEncoder.verifyToken(invalidToken, TEST_HASHED_TOKEN)).thenReturn(false);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> refreshTokenService.rotateToken(
                        new RequestRefreshTokenRotateDto(invalidToken, refreshTokenId)));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.INVALID_REFRESH_TOKEN.getCode());
    }

    @Test
    void rotateToken_shouldThrowExceptionWithInvalidRefreshTokenCode_whenUserNotFound() {
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshToken = "valid-refresh-token";
        UUID nonExistentUserId = UUID.randomUUID();
        when(hashOps.entries("RefreshToken-" + refreshTokenId))
                .thenReturn(Map.of("userId", nonExistentUserId.toString(), "tokenValue", TEST_HASHED_TOKEN));
        when(refreshTokenEncoder.verifyToken(refreshToken, TEST_HASHED_TOKEN)).thenReturn(true);
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class,
                () -> refreshTokenService.rotateToken(
                        new RequestRefreshTokenRotateDto(refreshToken, refreshTokenId)));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.INVALID_REFRESH_TOKEN.getCode());
    }

    private void setupValidRotateTokenMocks(String refreshTokenId, String refreshToken) {
        when(hashOps.entries("RefreshToken-" + refreshTokenId))
                .thenReturn(Map.of("userId", testUser.getId().toString(), "tokenValue", TEST_HASHED_TOKEN));
        when(refreshTokenEncoder.verifyToken(refreshToken, TEST_HASHED_TOKEN)).thenReturn(true);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(accessTokenService.createToken(testUser))
                .thenReturn(new CreateAccessTokenResponseDto("new-access-token"));
        when(refreshTokenEncoder.encodeToken(anyString())).thenReturn(TEST_ENCODED_TOKEN);
    }
}
