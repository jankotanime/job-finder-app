package com.mimaja.job_finder_app.security.authorization.register.service;import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.authorization.register.utils.DefaultRegisterDataManager;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.shared.dto.RequestRegisterDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseRefreshTokenDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
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
  private final DefaultRegisterDataManager defaultRegisterDataValidation;

  @Override
  public ResponseTokenDto tryToRegister(RequestRegisterDto reqData) {
    String username = reqData.username();
    String email = reqData.email();
    String password = reqData.password();
    String phoneNumberString = reqData.phoneNumber();

    defaultRegisterDataValidation.checkDataPatterns(username, email, phoneNumberString, password);

    int phoneNumber = defaultRegisterDataValidation.convertePhoneNumberToInt(phoneNumberString);
    defaultRegisterDataValidation.checkUserExistence(username, email, phoneNumber);

    String hashedPassword = passwordConfiguration.passwordEncoder().encode(password);

    User user = new User(username, email, hashedPassword, phoneNumber);
    userRepository.save(user);

    UUID userId = user.getId();

    String accessToken = jwtConfiguration.createToken(userId, username);

    ResponseRefreshTokenDto refreshToken = refreshTokenServiceDefault.createToken(user.getId());
    ResponseTokenDto tokens =
        new ResponseTokenDto(
            accessToken, refreshToken.refreshToken(), refreshToken.refreshTokenId());

    return tokens;
  }
}
