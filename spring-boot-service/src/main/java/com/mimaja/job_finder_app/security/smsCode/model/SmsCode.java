package com.mimaja.job_finder_app.security.smsCode.model;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("SmsCode")
@Getter
@AllArgsConstructor
public class SmsCode implements Serializable {
    @Id private final String userId;
    private final String hashedCode;
    private final String expiresAt;

    public SmsCode(Map<String, String> codeData) {
        this.userId = codeData.get("userId");
        this.hashedCode = codeData.get("codeValue");
        this.expiresAt = codeData.get("expiresAt");
    }
}
