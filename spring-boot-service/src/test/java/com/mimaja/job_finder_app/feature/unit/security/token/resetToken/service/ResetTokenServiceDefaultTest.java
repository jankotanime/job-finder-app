package com.mimaja.job_finder_app.feature.unit.security.token.resetToken.service;

import static com.mimaja.job_finder_app.feature.unit.security.mockdata.SecurityMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.encoder.ResetTokenEncoder;
import com.mimaja.job_finder_app.security.token.resetToken.dto.response.ResetTokenResponseDto;
import com.mimaja.job_finder_app.security.token.resetToken.service.ResetTokenServiceDefault;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

@ExtendWith(MockitoExtension.class)
class ResetTokenServiceDefaultTest {
    private static final String ENCODED_RESET_TOKEN = "encoded-reset-token";
    private static final String HASHED_TOKEN_VALUE = "hashed-token-value";
    private static final String VALID_RESET_TOKEN = "valid-reset-token";
    private static final String INVALID_RESET_TOKEN = "invalid-reset-token";
    private static final String RESET_TOKEN_KEY_PREFIX = "ResetToken-";
    private static final int TOKEN_TTL_SECONDS = 900;

    @Mock private StringRedisTemplate redisTemplate;

    @Mock private HashOperations<String, Object, Object> hashOps;

    @Mock private ResetTokenEncoder resetTokenEncoder;

    @Mock private UserRepository userRepository;

    private ResetTokenServiceDefault resetTokenService;

    private User testUser;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOps);
        testUser = createTestUser();
        resetTokenService =
                new ResetTokenServiceDefault(redisTemplate, resetTokenEncoder, userRepository);
    }

    @Test
    void deleteToken_shouldInvokeRedisDeleteOnce_whenTokenIdIsValid() {
        String tokenId = UUID.randomUUID().toString();

        resetTokenService.deleteToken(tokenId);

        verify(redisTemplate, times(1)).delete(RESET_TOKEN_KEY_PREFIX + tokenId);
    }

    @Test
    void createToken_shouldReturnNonNullResponse_whenUserIdIsValid() {
        when(resetTokenEncoder.encodeToken(anyString())).thenReturn(ENCODED_RESET_TOKEN);

        ResetTokenResponseDto result = resetTokenService.createToken(testUser.getId());

        assertThat(result).isNotNull();
    }

    @Test
    void createToken_shouldReturnNonBlankResetToken_whenUserIdIsValid() {
        when(resetTokenEncoder.encodeToken(anyString())).thenReturn(ENCODED_RESET_TOKEN);

        ResetTokenResponseDto result = resetTokenService.createToken(testUser.getId());

        assertThat(result.resetToken()).isNotBlank();
    }

    @Test
    void createToken_shouldReturnNonBlankResetTokenId_whenUserIdIsValid() {
        when(resetTokenEncoder.encodeToken(anyString())).thenReturn(ENCODED_RESET_TOKEN);

        ResetTokenResponseDto result = resetTokenService.createToken(testUser.getId());

        assertThat(result.resetTokenId()).isNotBlank();
    }

    @Test
    void createToken_shouldCallOpsForHash_whenUserIdIsValid() {
        when(resetTokenEncoder.encodeToken(anyString())).thenReturn(ENCODED_RESET_TOKEN);

        resetTokenService.createToken(testUser.getId());

        verify(redisTemplate, times(1)).opsForHash();
    }

    @Test
    void createToken_shouldEncodeTokenOnce_whenUserIdIsValid() {
        when(resetTokenEncoder.encodeToken(anyString())).thenReturn(ENCODED_RESET_TOKEN);

        resetTokenService.createToken(testUser.getId());

        verify(resetTokenEncoder, times(1)).encodeToken(anyString());
    }

    @Test
    void validateToken_shouldReturnUserWithMatchingId_whenTokenIsValid() {
        String tokenId = UUID.randomUUID().toString();
        stubValidTokenEntries(tokenId, testUser.getId());

        when(resetTokenEncoder.verifyToken(VALID_RESET_TOKEN, HASHED_TOKEN_VALUE)).thenReturn(true);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        User result = resetTokenService.validateToken(VALID_RESET_TOKEN, tokenId);

        assertThat(result.getId()).isEqualTo(testUser.getId());
    }

    @Test
    void validateToken_shouldReturnUserWithMatchingUsername_whenTokenIsValid() {
        String tokenId = UUID.randomUUID().toString();
        stubValidTokenEntries(tokenId, testUser.getId());

        when(resetTokenEncoder.verifyToken(VALID_RESET_TOKEN, HASHED_TOKEN_VALUE)).thenReturn(true);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        User result = resetTokenService.validateToken(VALID_RESET_TOKEN, tokenId);

        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    void validateToken_shouldVerifyTokenWithEncoder_whenTokenIsValid() {
        String tokenId = UUID.randomUUID().toString();
        stubValidTokenEntries(tokenId, testUser.getId());

        when(resetTokenEncoder.verifyToken(VALID_RESET_TOKEN, HASHED_TOKEN_VALUE)).thenReturn(true);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        resetTokenService.validateToken(VALID_RESET_TOKEN, tokenId);

        verify(resetTokenEncoder, times(1)).verifyToken(VALID_RESET_TOKEN, HASHED_TOKEN_VALUE);
    }

    @Test
    void validateToken_shouldFindUserById_whenTokenIsValid() {
        String tokenId = UUID.randomUUID().toString();
        stubValidTokenEntries(tokenId, testUser.getId());

        when(resetTokenEncoder.verifyToken(VALID_RESET_TOKEN, HASHED_TOKEN_VALUE)).thenReturn(true);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        resetTokenService.validateToken(VALID_RESET_TOKEN, tokenId);

        verify(userRepository, times(1)).findById(testUser.getId());
    }

    @Test
    void validateToken_shouldDeleteRedisKey_whenTokenIsValid() {
        String tokenId = UUID.randomUUID().toString();
        stubValidTokenEntries(tokenId, testUser.getId());

        when(resetTokenEncoder.verifyToken(VALID_RESET_TOKEN, HASHED_TOKEN_VALUE)).thenReturn(true);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        resetTokenService.validateToken(VALID_RESET_TOKEN, tokenId);

        verify(redisTemplate, times(1)).delete(RESET_TOKEN_KEY_PREFIX + tokenId);
    }

    @Test
    void validateToken_shouldThrowBusinessExceptionWithInvalidResetTokenCode_whenTokenIsInvalid() {
        String tokenId = UUID.randomUUID().toString();
        stubValidTokenEntries(tokenId, testUser.getId());

        when(resetTokenEncoder.verifyToken(INVALID_RESET_TOKEN, HASHED_TOKEN_VALUE))
                .thenReturn(false);

        assertThatThrownBy(() -> resetTokenService.validateToken(INVALID_RESET_TOKEN, tokenId))
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessException.class))
                .extracting(BusinessException::getCode)
                .isEqualTo(BusinessExceptionReason.INVALID_RESET_TOKEN.getCode());
    }

    @Test
    void validateToken_shouldThrowBusinessExceptionWithInvalidResetTokenCode_whenUserNotFound() {
        String tokenId = UUID.randomUUID().toString();
        UUID nonExistentUserId = UUID.randomUUID();
        stubValidTokenEntries(tokenId, nonExistentUserId);

        when(resetTokenEncoder.verifyToken(VALID_RESET_TOKEN, HASHED_TOKEN_VALUE)).thenReturn(true);
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resetTokenService.validateToken(VALID_RESET_TOKEN, tokenId))
                .asInstanceOf(InstanceOfAssertFactories.type(BusinessException.class))
                .extracting(BusinessException::getCode)
                .isEqualTo(BusinessExceptionReason.INVALID_RESET_TOKEN.getCode());
    }

    private void stubValidTokenEntries(String tokenId, UUID userId) {
        when(hashOps.entries(RESET_TOKEN_KEY_PREFIX + tokenId))
                .thenReturn(
                        Map.of(
                                "userId",
                                userId.toString(),
                                "tokenValue",
                                HASHED_TOKEN_VALUE,
                                "expiresAt",
                                Instant.now().plusSeconds(TOKEN_TTL_SECONDS).toString()));
    }
}
