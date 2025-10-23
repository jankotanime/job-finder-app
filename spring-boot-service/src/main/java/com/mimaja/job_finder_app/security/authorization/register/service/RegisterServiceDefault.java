package com.mimaja.job_finder_app.security.authorization.register.service;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.shared.dto.RequestRegisterDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseRefreshTokenDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokensDto;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;
import com.mimaja.job_finder_app.security.tokens.refreshTokens.service.RefreshTokenServiceDefault;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterServiceDefault implements RegisterService {
  private final JwtConfiguration jwtConfiguration;
  private final PasswordConfiguration passwordConfiguration;
  private final UserRepository userRepository;
  private final RefreshTokenServiceDefault refreshTokenServiceDefault;

  @Override
  public boolean patternMatches(String emailAddress, String regexPattern) {
    return Pattern.compile(regexPattern)
      .matcher(emailAddress)
      .matches();
  }

  @Override
  public ResponseTokensDto tryToRegister(RequestRegisterDto reqData) {
    String username = reqData.username();
    String email = reqData.email();
    String password = reqData.password();
    String phoneNumberString = reqData.phoneNumber();

    if (username.length() < 4) {
      throw new BusinessException(BusinessExceptionReason.INVALID_USERNAME);
    }

    if (phoneNumberString.length() != 9) {
      throw new BusinessException(BusinessExceptionReason.INVALID_PHONE_NUMBER);
    }

    if (!patternMatches(email, "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+.[a-zA-Z0-9.-]+$")) {
      throw new BusinessException(BusinessExceptionReason.INVALID_EMAIL);
    }

    if (!patternMatches(password, "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
      throw new BusinessException(BusinessExceptionReason.INVALID_PASSWORD);
    }

    int phoneNumber;

    try {
      phoneNumber = Integer.parseInt(phoneNumberString);
    }
    catch (NumberFormatException e) {
      throw new BusinessException(BusinessExceptionReason.INVALID_PHONE_NUMBER);
    }

    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent()) {
      throw new BusinessException(BusinessExceptionReason.USERNAME_TAKEN);
    }

    userOptional = userRepository.findByEmail(email);
    if (userOptional.isPresent()) {
      throw new BusinessException(BusinessExceptionReason.EMAIL_TAKEN);
    }

    userOptional = userRepository.findByPhoneNumber(phoneNumber);
    if (userOptional.isPresent()) {
      throw new BusinessException(BusinessExceptionReason.PHONE_NUMBER_TAKEN);
    }

    String hashedPassword = passwordConfiguration.passwordEncoder().encode(password);

    User user = new User(username, email, hashedPassword, phoneNumber);
    userRepository.save(user);

    UUID userId = user.getId();

    String accessToken = jwtConfiguration.createToken(userId, username);

    ResponseRefreshTokenDto refreshToken = refreshTokenServiceDefault.createToken(user.getId());
    ResponseTokensDto tokens = new ResponseTokensDto(
      accessToken,
      refreshToken.refreshToken(),
      refreshToken.refreshTokenId()
    );

    return tokens;
  }
}
