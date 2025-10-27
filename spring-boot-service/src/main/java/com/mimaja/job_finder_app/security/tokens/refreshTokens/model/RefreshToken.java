package com.mimaja.job_finder_app.security.tokens.refreshTokens.model;import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("refreshToken")
@Getter
@AllArgsConstructor
public class RefreshToken implements Serializable {
  @Id private final String id;
  private final String userId;
  private final String hashedToken;
  private final String expiresAt;
}
