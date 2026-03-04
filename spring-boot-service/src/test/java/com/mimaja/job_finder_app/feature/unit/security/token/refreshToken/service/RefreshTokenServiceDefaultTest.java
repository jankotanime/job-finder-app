package com.mimaja.job_finder_app.feature.unit.security.token.refreshToken.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.security.encoder.RefreshTokenEncoder;
import com.mimaja.job_finder_app.security.token.accessToken.service.AccessTokenService;
import com.mimaja.job_finder_app.security.token.refreshToken.dto.response.RefreshTokenResponseDto;
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
    @DisplayName("Should delete all user tokens")
    void testDeleteAllTokens_WithValidUser_ShouldReturnToken() {
        setUpReturnSetOpsMock();

        UUID userId = testUser.getId();
        refreshTokenService.deleteAllUserTokens(userId);

        String expectedKey = "userTokens:" + userId;
        verify(setOps, times(1)).members(expectedKey);
    }
}
