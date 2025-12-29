package com.mimaja.job_finder_app.security.token.resetToken.service;

import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.security.token.resetToken.dto.response.ResetTokenResponseDto;
import java.util.UUID;

public interface ResetTokenService {
    ResetTokenResponseDto createToken(UUID userId);

    void deleteToken(String tokenId);

    User validateToken(String token, String tokenId);
}
