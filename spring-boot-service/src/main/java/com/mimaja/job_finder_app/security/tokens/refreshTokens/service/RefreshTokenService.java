package com.mimaja.job_finder_app.security.tokens.refreshTokens.service;import com.mimaja.job_finder_app.security.shared.dto.RequestRefreshTokenRotateDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseRefreshTokenDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import java.util.UUID;

public interface RefreshTokenService {
  public ResponseRefreshTokenDto createToken(UUID userId);

  public void deleteToken(String tokenId);

  public ResponseTokenDto rotateToken(RequestRefreshTokenRotateDto reqData);

  public void deleteAllUserTokens(UUID userId);
}
