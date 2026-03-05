package com.mimaja.job_finder_app.feature.unit.security.token.resetToken.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.security.encoder.ResetTokenEncoder;
import com.mimaja.job_finder_app.security.token.resetToken.dto.response.ResetTokenResponseDto;
import com.mimaja.job_finder_app.security.token.resetToken.service.ResetTokenServiceDefault;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResetTokenServiceDefault - Unit Tests")
public class ResetTokenServiceDefaultTest {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOps;

    @Mock
    private ResetTokenEncoder resetTokenEncoder;

    @Mock
    private UserRepository userRepository;

    private ResetTokenServiceDefault resetTokenService;

    private User testUser;

    void setUp() {
        testUser = createTestUser();

        resetTokenService = new ResetTokenServiceDefault(
            redisTemplate,
            resetTokenEncoder,
            userRepository
        );
    }

    private void setUpForCreatingValidToken() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(resetTokenEncoder.encodeToken(anyString()))
            .thenReturn("encoded-reset-token");

        setUp();
    }

    private void setUpOpsForHashToken() {
        when(redisTemplate.opsForHash()).thenReturn(hashOps);

        setUp();
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

    @Test
    @DisplayName("Should delete reset token by token id")
    void testDeleteToken_WithValidTokenId_ShouldDeleteToken() {
        setUp();

        String tokenId = UUID.randomUUID().toString();
        resetTokenService.deleteToken(tokenId);

        String expectedKey = "ResetToken-" + tokenId;
        verify(redisTemplate, times(1)).delete(expectedKey);
    }

    @Test
    @DisplayName("Should create reset token for valid user")
    void testCreateToken_WithValidUser_ShouldReturnResetTokenResponseDto() {
        setUpForCreatingValidToken();

        UUID userId = testUser.getId();
        ResetTokenResponseDto result = resetTokenService.createToken(userId);

        assertNotNull(result, "ResetTokenResponseDto should not be null");
        assertThat(result.resetToken())
            .as("Reset token should not be empty")
            .isNotBlank();
        assertThat(result.resetTokenId())
            .as("Reset token ID should not be empty")
            .isNotBlank();

        verify(redisTemplate, times(1)).opsForHash();
        verify(resetTokenEncoder, times(1)).encodeToken(anyString());
    }

    @Test
    @DisplayName("Should validate token successfully with valid reset token")
    void testValidateToken_WithValidResetToken_ShouldReturnUser() {
        setUpOpsForHashToken();

        String tokenId = UUID.randomUUID().toString();
        String hashedToken = "hashed-token-value";
        String resetToken = "valid-reset-token";

        when(hashOps.entries("ResetToken-" + tokenId))
            .thenReturn(java.util.Map.of(
                "userId", testUser.getId().toString(),
                "tokenValue", hashedToken,
                "expiresAt", java.time.Instant.now().plusSeconds(900).toString()
            ));

        when(resetTokenEncoder.verifyToken(resetToken, hashedToken)).thenReturn(true);

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        User result = resetTokenService.validateToken(resetToken, tokenId);

        assertNotNull(result, "User should not be null");
        assertThat(result.getId())
            .as("User ID should match")
            .isEqualTo(testUser.getId());
        assertThat(result.getUsername())
            .as("Username should match")
            .isEqualTo(testUser.getUsername());

        verify(resetTokenEncoder, times(1)).verifyToken(resetToken, hashedToken);
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(redisTemplate, times(1)).delete("ResetToken-" + tokenId);
    }

    @Test
    @DisplayName("Should throw BusinessException when reset token is invalid")
    void testValidateToken_WithInvalidResetToken_ShouldThrowBusinessException() {
        setUpOpsForHashToken();

        String tokenId = UUID.randomUUID().toString();
        String hashedToken = "hashed-token-value";
        String invalidResetToken = "invalid-reset-token";

        when(hashOps.entries("ResetToken-" + tokenId))
            .thenReturn(java.util.Map.of(
                "userId", testUser.getId().toString(),
                "tokenValue", hashedToken,
                "expiresAt", java.time.Instant.now().plusSeconds(900).toString()
            ));

        when(resetTokenEncoder.verifyToken(invalidResetToken, hashedToken)).thenReturn(false);

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> resetTokenService.validateToken(invalidResetToken, tokenId),
            "Should throw BusinessException for invalid reset token"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate invalid reset token")
            .isEqualTo(BusinessExceptionReason.INVALID_RESET_TOKEN.getCode());
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not found")
    void testValidateToken_WithNonExistentUser_ShouldThrowBusinessException() {
        setUpOpsForHashToken();

        String tokenId = UUID.randomUUID().toString();
        String hashedToken = "hashed-token-value";
        String resetToken = "valid-reset-token";
        UUID nonExistentUserId = UUID.randomUUID();

        when(hashOps.entries("ResetToken-" + tokenId))
            .thenReturn(java.util.Map.of(
                "userId", nonExistentUserId.toString(),
                "tokenValue", hashedToken,
                "expiresAt", java.time.Instant.now().plusSeconds(900).toString()
            ));

        when(resetTokenEncoder.verifyToken(resetToken, hashedToken)).thenReturn(true);

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> resetTokenService.validateToken(resetToken, tokenId),
            "Should throw BusinessException when user is not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate invalid reset token")
            .isEqualTo(BusinessExceptionReason.INVALID_RESET_TOKEN.getCode());
    }
}
