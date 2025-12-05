package com.mimaja.job_finder_app.security.smsCode.service;

import java.util.UUID;

public interface SmsCodeService {
    void createCode(UUID userId);

    void deleteCode(String codeId);

    void validateCode(UUID userId, int token);
}
