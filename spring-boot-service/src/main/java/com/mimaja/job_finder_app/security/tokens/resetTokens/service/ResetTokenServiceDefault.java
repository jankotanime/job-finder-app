package com.mimaja.job_finder_app.security.tokens.resetTokens.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.shared.dto.ResponseResetTokenDto;
import com.mimaja.job_finder_app.security.tokens.encoder.RefreshTokenEncoder;
import com.mimaja.job_finder_app.security.tokens.refreshTokens.model.RefreshToken;

public class ResetTokenServiceDefault implements ResetTokenService {
  private final StringRedisTemplate redisTemplate;
  private final HashOperations<String, String, String> hashOps;
  private final RefreshTokenEncoder refreshTokenEncoder;
  private final UserRepository userRepository;

  public ResetTokenServiceDefault(
    StringRedisTemplate redisTemplate,
    RefreshTokenEncoder refreshTokenEncoder,
    UserRepository userRepository) {
    this.redisTemplate = redisTemplate;
    this.hashOps = redisTemplate.opsForHash();
    this.refreshTokenEncoder = refreshTokenEncoder;
    this.userRepository = userRepository;
  }

  @Override
  public void deleteToken(String tokenId) {
    redisTemplate.delete("ResetToken-" + tokenId);
  }

  @Override
  public ResponseResetTokenDto createToken(UUID userId) {
    String resetTokenId = UUID.randomUUID().toString();
    String refreshTokenKey = "ResetToken-" + resetTokenId;
    String resetTokenValue = UUID.randomUUID().toString();

    String hashedResetTokenValue = refreshTokenEncoder.encodeToken(resetTokenValue);

    int lifetimeMinutes = 15;
    Instant expiresAt = Instant.now().plusSeconds(lifetimeMinutes * 60);

    hashOps.put(refreshTokenKey, "tokenValue", hashedResetTokenValue);
    hashOps.put(refreshTokenKey, "userId", userId.toString());
    hashOps.put(refreshTokenKey, "expiresAt", expiresAt.toString());
    redisTemplate.expire(refreshTokenKey, lifetimeMinutes, TimeUnit.MINUTES);

    ResponseResetTokenDto result = new ResponseResetTokenDto(resetTokenValue, resetTokenId);

    return result;
  }

  @Override
  public User validateToken(String token, String tokenId) {
    RefreshToken tokenData =
        new RefreshToken(tokenId, hashOps.entries("RefreshToken-" + token));
    System.out.println(tokenData.getHashedToken());

    if (!refreshTokenEncoder.verifyToken(token, tokenData.getHashedToken())) {
      throw new BusinessException(BusinessExceptionReason.INVALID_REFRESH_TOKEN);
    }

    deleteToken(tokenId);

    UUID userId = UUID.fromString(tokenData.getUserId());
    Optional<User> userOptional = userRepository.findById(userId);

    if (userOptional.isEmpty()) {
      throw new BusinessException(BusinessExceptionReason.INVALID_REFRESH_TOKEN);
    }

    return userOptional.get();
  }
}
