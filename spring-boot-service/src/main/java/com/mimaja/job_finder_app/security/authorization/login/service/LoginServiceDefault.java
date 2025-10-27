package com.mimaja.job_finder_app.security.authorization.login.service;import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;
import com.mimaja.job_finder_app.security.tokens.refreshTokens.service.RefreshTokenServiceDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceDefault implements LoginService {
  private final JwtConfiguration jwtConfiguration;
  private final RefreshTokenServiceDefault refreshTokenServiceDefault;
  private final UserRepository userRepository;
  private final PasswordConfiguration passwordConfiguration;

  @Override
  public Map<String, String> tryToLogin(Map<String, String> reqData) {
    Map<String, String> response = new HashMap<>();

    if (!reqData.containsKey("loginData") || !reqData.containsKey("password")) {
      response.put("err", "Invalid body!");
      return response;
    }

    String loginData = reqData.get("loginData");
    String password = reqData.get("password");

    Optional<User> userOptional = userRepository.findByUsername(loginData);

    if (userOptional.isEmpty()) {
      userOptional = userRepository.findByEmail(loginData);
    }

    if (userOptional.isEmpty()) {
      try {
        int phoneNumber = Integer.parseInt(loginData);
        userOptional = userRepository.findByPhoneNumber(phoneNumber);
      } catch (NumberFormatException e) {
        response.put("err", "User not found!");
        return response;
      }
    }

    if (userOptional.isEmpty()) {
      response.put("err", "User not found!");
      return response;
    }

    User user = userOptional.get();

    if (!passwordConfiguration.verifyPassword(password, user.getPasswordHash())) {
      response.put("err", "Password does not match");
      return response;
    }

    String accessToken = jwtConfiguration.createToken(user.getId(), user.getUsername());
    response = refreshTokenServiceDefault.createToken(user.getId());

    response.put("accessToken", accessToken);

    return response;
  }
}
