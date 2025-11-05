package com.mimaja.job_finder_app.security.tokens.resetTokens.service;

import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.security.shared.dto.ResponseResetTokenDto;
import java.util.UUID;

public interface ResetTokenService {
    ResponseResetTokenDto createToken(UUID userId);

    void deleteToken(String tokenId);

    User validateToken(String token, String tokenId);
}
