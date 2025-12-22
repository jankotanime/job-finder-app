package com.mimaja.job_finder_app.security.token.refreshToken.service;

import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import com.mimaja.job_finder_app.security.token.refreshToken.dto.request.RequestRefreshTokenRotateDto;
import com.mimaja.job_finder_app.security.token.refreshToken.dto.response.RefreshTokenResponseDto;
import java.util.UUID;

public interface RefreshTokenService {
    RefreshTokenResponseDto createRefreshToken(User user);

    void deleteToken(String tokenId);

    ResponseTokenDto rotateToken(RequestRefreshTokenRotateDto reqData);

    ResponseTokenDto createTokensSet(User user);

    void deleteAllUserTokens(UUID userId);
}
