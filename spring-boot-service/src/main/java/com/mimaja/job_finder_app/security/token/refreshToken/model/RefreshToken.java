package com.mimaja.job_finder_app.security.token.refreshToken.model;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("RefreshToken")
@Getter
@AllArgsConstructor
public class RefreshToken implements Serializable {
    @Id private final String id;
    private final String userId;
    private final String hashedToken;
    private final String expiresAt;

    public RefreshToken(String id, Map<String, String> tokenData) {
        this.id = id;
        this.userId = tokenData.get("userId");
        this.hashedToken = tokenData.get("tokenValue");
        this.expiresAt = tokenData.get("expiresAt");
    }
}
