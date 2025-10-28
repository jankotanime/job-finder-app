package com.mimaja.job_finder_app.security.authorization.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.security.authorization.login.utils.DefaultLoginValidation;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.shared.dto.RequestLoginDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseRefreshTokenDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;
import com.mimaja.job_finder_app.security.tokens.refreshTokens.service.RefreshTokenServiceDefault;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceDefault implements LoginService {
  private final JwtConfiguration jwtConfiguration;
  private final RefreshTokenServiceDefault refreshTokenServiceDefault;
  private final PasswordConfiguration passwordConfiguration;
  private final DefaultLoginValidation defaultLoginValidation;

  @Override
  public ResponseTokenDto tryToLogin(RequestLoginDto reqData) {
    String loginData = reqData.loginData();
    String password = reqData.password();

    User user = defaultLoginValidation.userValidation(loginData, password);

    String username = user.getUsername();
    UUID userId = user.getId();

    if (!passwordConfiguration.verifyPassword(password, user.getPasswordHash())) {
      throw new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA);
    }

    String accessToken = jwtConfiguration.createToken(userId, username);

    ResponseRefreshTokenDto refreshToken = refreshTokenServiceDefault.createToken(userId);

    ResponseTokenDto tokens = new ResponseTokenDto(
      accessToken,
      refreshToken.refreshToken(),
      refreshToken.refreshTokenId()
    );

    return tokens;
  }
}
