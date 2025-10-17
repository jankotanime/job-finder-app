package com.mimaja.job_finder_app.security.tokens.refreshTokens.service;

import java.util.Map;
import java.util.UUID;

public interface RefreshTokenService {
  public Map<String, String> createToken(UUID userId);
  public void deleteToken(String tokenId);
  public Map<String, String> rotateToken(Map<String, String> reqData);
  public void deleteAllUserTokens(UUID userId);
}
