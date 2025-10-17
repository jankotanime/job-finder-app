package com.mimaja.job_finder_app.security.tokens.refreshTokens.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.tokens.encoder.RefreshTokenEncoder;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RefreshTokenServiceDefault implements RefreshTokenService {
  private final StringRedisTemplate redisTemplate;
  private final HashOperations<String, String, String> hashOps;
  private final RefreshTokenEncoder refreshTokenEncoder;
  private final JwtConfiguration jwtConfiguration;
  private final UserRepository userRepository;

  public RefreshTokenServiceDefault(StringRedisTemplate redisTemplate,
    RefreshTokenEncoder refreshTokenEncoder, JwtConfiguration jwtConfiguration,
    UserRepository userRepository) {
    this.redisTemplate = redisTemplate;
    this.hashOps = redisTemplate.opsForHash();
    this.refreshTokenEncoder = refreshTokenEncoder;
    this.jwtConfiguration = jwtConfiguration;
    this.userRepository = userRepository;
  }

  @Override
  public Map<String, String> createToken(UUID userId) {
    Map<String, String> response = new HashMap<String,String>();

    String refreshTokenId = UUID.randomUUID().toString();
    String refreshTokenKey = "RefreshToken-" + refreshTokenId;
    String refreshTokenValue = UUID.randomUUID().toString();

    String hashedRefreshTokenValue = refreshTokenEncoder.encodeToken(refreshTokenValue);

    int lifetimeDays = 30;
    LocalDate expiresAt = LocalDate.now().plusDays(30);

    hashOps.put(refreshTokenKey, "tokenValue", hashedRefreshTokenValue);
    hashOps.put(refreshTokenKey, "userId", userId.toString());
    hashOps.put(refreshTokenKey, "expiresAt", expiresAt.toString());
    redisTemplate.expire(refreshTokenKey, lifetimeDays, TimeUnit.DAYS);

    response.put("refreshToken", refreshTokenValue);
    response.put("refreshTokenId", refreshTokenId);
    return response;
  }

  @Override
  public void deleteToken(String tokenId) {
    redisTemplate.delete("RefreshToken-" + tokenId);
  }

  @Override
  public Map<String, String> rotateToken(Map<String, String> reqData) {
    Map<String, String> response = new HashMap<String,String>();

    if (!reqData.containsKey("refreshToken") || !reqData.containsKey("refreshTokenId")) {
      response.put("err", "Invalid body!");
      return response;
    }

    String refreshToken = reqData.get("refreshToken");
    String refreshTokenId = reqData.get("refreshTokenId");

    Map<String, String> tokenData = hashOps.entries("RefreshToken-" + refreshTokenId);

    if (!refreshTokenEncoder.verifyToken(refreshToken, tokenData.get("tokenValue"))) {
      response.put("err", "Invalid refresh token!");
      return response;
    }

    System.out.println(tokenData);

    deleteToken(refreshTokenId);

    User user;
    try {
      Optional<User> userOptional = userRepository.findById(UUID.fromString(tokenData.get("userId")));
      user = userOptional.get();
    } catch (InternalError e) {
      response.put("err", "User does not exist");
      return response;
    }

    String accessToken = jwtConfiguration.createToken(user.getId(), user.getUsername());
    response = createToken(user.getId());
    response.put("accessToken", accessToken);
    return response;
  }

  @Override
  public void deleteAllUserTokens(UUID userId) {
    Set<String> tokenIds = redisTemplate.opsForSet().members("userTokens:" + userId);

    if (tokenIds != null && !tokenIds.isEmpty()) {
      List<String> keysToDelete = tokenIds.stream()
        .map(id -> "refreshToken:" + id)
        .toList();

      redisTemplate.delete(keysToDelete);
      redisTemplate.delete("userTokens:" + userId);
    }
  }
}
