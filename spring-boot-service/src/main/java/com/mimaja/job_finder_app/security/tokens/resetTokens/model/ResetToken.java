package com.mimaja.job_finder_app.security.tokens.resetTokens.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;

@RedisHash("resetToken")
@Getter
@AllArgsConstructor
public class ResetToken implements Serializable {
  @Id private final String id;
  private final String userId;
  private final String hashedToken;
  private final String expiresAt;

  public ResetToken(String id, Map<String, String> tokenData) {
    this.id = id;
    this.userId = tokenData.get("userId");
    this.hashedToken = tokenData.get("tokenValue");
    this.expiresAt = tokenData.get("expiresAt");
  }
}
