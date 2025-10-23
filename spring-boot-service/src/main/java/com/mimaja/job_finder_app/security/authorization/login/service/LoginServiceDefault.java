package com.mimaja.job_finder_app.security.authorization.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.shared.dto.RequestLoginDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseRefreshTokenDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokensDto;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;
import com.mimaja.job_finder_app.security.tokens.refreshTokens.service.RefreshTokenServiceDefault;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceDefault implements LoginService {
  private final JwtConfiguration jwtConfiguration;
  private final RefreshTokenServiceDefault refreshTokenServiceDefault;
  private final UserRepository userRepository;
  private final PasswordConfiguration passwordConfiguration;

  @Override
  public ResponseTokensDto tryToLogin(RequestLoginDto reqData) {
    String loginData = reqData.loginData();
    String password = reqData.password();

    Optional<User> userOptional = userRepository.findByUsername(loginData);

    if (userOptional.isEmpty()) {
      userOptional = userRepository.findByEmail(loginData);
    }

    if (userOptional.isEmpty()) {
      try {
        int phoneNumber = Integer.parseInt(loginData);
        userOptional = userRepository.findByPhoneNumber(phoneNumber);
      }
      catch (NumberFormatException e) {
        throw new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA);
      }
    }

    if (userOptional.isEmpty()) {
      throw new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA);
    }

    User user = userOptional.get();

    String username = user.getUsername();
    UUID userId = user.getId();

    if (!passwordConfiguration.verifyPassword(password, user.getPasswordHash())) {
      throw new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA);
    }

    String accessToken = jwtConfiguration.createToken(userId, username);

    ResponseRefreshTokenDto refreshToken = refreshTokenServiceDefault.createToken(userId);

    ResponseTokensDto tokens = new ResponseTokensDto(
      accessToken,
      refreshToken.refreshToken(),
      refreshToken.refreshTokenId()
    );

    return tokens;
  }
}
