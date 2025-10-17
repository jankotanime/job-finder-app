package com.mimaja.job_finder_app.security.tokens.refreshTokens.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mimaja.job_finder_app.security.tokens.encoder.RefreshTokenEncoder;
import com.mimaja.job_finder_app.security.tokens.jwt.authorizationFilter.JwtPrincipal;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RefreshTokenServiceDefault implements RefreshTokenService {
  private final StringRedisTemplate redisTemplate;
  private final HashOperations<String, String, String> hashOps;
  private final RefreshTokenEncoder refreshTokenEncoder;

  public RefreshTokenServiceDefault(StringRedisTemplate redisTemplate, RefreshTokenEncoder refreshTokenEncoder) {
    this.redisTemplate = redisTemplate;
    this.hashOps = redisTemplate.opsForHash();
    this.refreshTokenEncoder = refreshTokenEncoder;
  }

  @Override
  public Map<String, String> saveToken() {
    Map<String, String> response = new HashMap<String,String>();

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();

    UUID userId = principal.getId();

    String refreshTokenId = UUID.randomUUID().toString();
    String refreshTokenValue = UUID.randomUUID().toString();

    String hashedRefreshTokenValue = refreshTokenEncoder.encodeToken(refreshTokenValue);

    int lifetimeDays = 30;
    LocalDate expiresAt = LocalDate.now().plusDays(30);

    hashOps.put(refreshTokenId, "tokenValue", hashedRefreshTokenValue);
    hashOps.put(refreshTokenId, "userId", userId.toString());
    hashOps.put(refreshTokenId, "expiresAt", expiresAt.toString());
    redisTemplate.expire(refreshTokenId, lifetimeDays, TimeUnit.DAYS);

    response.put("refreshToken", refreshTokenValue);
    response.put("refreshTokenId", refreshTokenId);
    return response;
  }
}
