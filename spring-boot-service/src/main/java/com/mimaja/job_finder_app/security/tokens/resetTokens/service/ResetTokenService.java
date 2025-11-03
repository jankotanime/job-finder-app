package com.mimaja.job_finder_app.security.tokens.resetTokens.service;

import java.util.UUID;

import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.security.shared.dto.ResponseResetTokenDto;

public interface ResetTokenService {
  public ResponseResetTokenDto createToken(UUID userId);

  public void deleteToken(String tokenId);

  public User validateToken(String token, String tokenId);
}
