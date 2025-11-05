package com.mimaja.job_finder_app.security.tokens.refreshTokens.service;

import com.mimaja.job_finder_app.security.shared.dto.RequestRefreshTokenRotateDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseRefreshTokenDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import java.util.UUID;

public interface RefreshTokenService {
    ResponseRefreshTokenDto createToken(UUID userId);

    void deleteToken(String tokenId);

    ResponseTokenDto rotateToken(RequestRefreshTokenRotateDto reqData);

    void deleteAllUserTokens(UUID userId);
}
