package com.mimaja.job_finder_app.security.smsCode.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.security.encoder.SmsCodeEncoder;
import com.mimaja.job_finder_app.security.smsCode.model.SmsCode;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SmsCodeServiceDefault implements SmsCodeService {
    private final StringRedisTemplate redisTemplate;
    private final HashOperations<String, String, String> hashOps;
    private final SmsCodeEncoder smsCodeEncoder;

    public SmsCodeServiceDefault(StringRedisTemplate redisTemplate, SmsCodeEncoder smsCodeEncoder) {
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
        this.smsCodeEncoder = smsCodeEncoder;
    }

    @Override
    public void deleteCode(String codeId) {
        redisTemplate.delete("SmsCode-" + codeId);
    }

    @Override
    public void createCode(UUID userId) {
        Random random = new Random();
        String smsCodeKey = "SmsCode-" + userId;
        int smsCodeValue = 100000 + random.nextInt(900000);

        String hashedSmsCodeValue = smsCodeEncoder.encodeCode(smsCodeValue);

        int lifetimeMinutes = 15;
        Instant expiresAt = Instant.now().plusSeconds(lifetimeMinutes * 60);

        hashOps.put(smsCodeKey, "userId", userId.toString());
        hashOps.put(smsCodeKey, "codeValue", hashedSmsCodeValue);
        hashOps.put(smsCodeKey, "expiresAt", expiresAt.toString());
        redisTemplate.expire(smsCodeKey, lifetimeMinutes, TimeUnit.MINUTES);

        System.out.println("Sms code for user with id: " + userId + "; code: " + smsCodeValue);
    }

    @Override
    public void validateCode(UUID userId, int code) {
        String tokenId = "SmsCode-" + userId;
        SmsCode smsCode = new SmsCode(hashOps.entries(tokenId));

        if (!smsCodeEncoder.verifyCode(code, smsCode.getHashedCode())) {
            throw new BusinessException(BusinessExceptionReason.INVALID_SMS_CODE);
        }

        deleteCode(tokenId);
    }
}
