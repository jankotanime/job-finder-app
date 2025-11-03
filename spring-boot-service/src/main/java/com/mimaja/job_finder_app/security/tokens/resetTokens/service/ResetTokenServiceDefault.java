package com.mimaja.job_finder_app.security.tokens.resetTokens.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.shared.dto.ResponseResetTokenDto;
import com.mimaja.job_finder_app.security.tokens.encoder.ResetTokenEncoder;
import com.mimaja.job_finder_app.security.tokens.resetTokens.model.ResetToken;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ResetTokenServiceDefault implements ResetTokenService {
  private final StringRedisTemplate redisTemplate;
  private final HashOperations<String, String, String> hashOps;
  private final ResetTokenEncoder resetTokenEncoder;
  private final UserRepository userRepository;

  public ResetTokenServiceDefault(
    StringRedisTemplate redisTemplate,
    ResetTokenEncoder resetTokenEncoder,
    UserRepository userRepository) {
    this.redisTemplate = redisTemplate;
    this.hashOps = redisTemplate.opsForHash();
    this.resetTokenEncoder = resetTokenEncoder;
    this.userRepository = userRepository;
  }

  @Override
  public void deleteToken(String tokenId) {
    redisTemplate.delete("ResetToken-" + tokenId);
  }

  @Override
  public ResponseResetTokenDto createToken(UUID userId) {
    String resetTokenId = UUID.randomUUID().toString();
    String resetTokenKey = "ResetToken-" + resetTokenId;
    String resetTokenValue = UUID.randomUUID().toString();

    String hashedResetTokenValue = resetTokenEncoder.encodeToken(resetTokenValue);
    System.out.println(hashedResetTokenValue);

    int lifetimeMinutes = 15;
    Instant expiresAt = Instant.now().plusSeconds(lifetimeMinutes * 60);

    hashOps.put(resetTokenKey, "tokenValue", hashedResetTokenValue);
    hashOps.put(resetTokenKey, "userId", userId.toString());
    hashOps.put(resetTokenKey, "expiresAt", expiresAt.toString());
    redisTemplate.expire(resetTokenKey, lifetimeMinutes, TimeUnit.MINUTES);

    ResponseResetTokenDto result = new ResponseResetTokenDto(resetTokenValue, resetTokenId);

    return result;
  }

  @Override
  public User validateToken(String token, String tokenId) {
    System.out.println(token);
    System.out.println(tokenId);
    ResetToken tokenData =
        new ResetToken(tokenId, hashOps.entries("ResetToken-" + tokenId));
    System.out.println(tokenData.getHashedToken());

    if (!resetTokenEncoder.verifyToken(token, tokenData.getHashedToken())) {
      throw new BusinessException(BusinessExceptionReason.INVALID_RESET_TOKEN);
    }

    UUID userId = UUID.fromString(tokenData.getUserId());
    Optional<User> userOptional = userRepository.findById(userId);

    if (userOptional.isEmpty()) {
      throw new BusinessException(BusinessExceptionReason.INVALID_RESET_TOKEN);
    }

    return userOptional.get();
  }
}
