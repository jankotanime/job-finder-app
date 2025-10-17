package com.mimaja.job_finder_app.security.authorization.register.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterServiceDefault implements RegisterService {
  private final JwtConfiguration jwtConfiguration;
  private final PasswordConfiguration passwordConfiguration;
  private final UserRepository userRepository;

  @Override
  public boolean patternMatches(String emailAddress, String regexPattern) {
    return Pattern.compile(regexPattern)
      .matcher(emailAddress)
      .matches();
  }

  @Override
  public Map<String, String> tryToRegister(Map<String, String> reqData) {
    Map<String, String> response = new HashMap<String,String>();
    if (!reqData.containsKey("username") || !reqData.containsKey("email")
    ||  !reqData.containsKey("phoneNumber") || !reqData.containsKey("password")) {
      response.put("err", "Invalid body!");
      return response;
    }

    String username = reqData.get("username");
    String email = reqData.get("email");
    String password = reqData.get("password");
    String phoneNumberString = reqData.get("phoneNumber");

    if (phoneNumberString.length() != 9) {
      response.put("err", "Wrong email");
      return response;
    }

    if (!patternMatches(email, "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+.[a-zA-Z0-9.-]+$")) {
      response.put("err", "Wrong email");
      return response;
    }

    if (!patternMatches(password, "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
      response.put("err", "Wrong password");
      return response;
    }

    int phoneNumber;

    try {
      phoneNumber = Integer.parseInt(phoneNumberString);
    }
    catch (NumberFormatException e) {
      response.put("err", "Invalid phone number format!");
      return response;
    }

    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent()) {
      response.put("err", "Username taken");
      return response;
    }

    userOptional = userRepository.findByEmail(email);
    if (userOptional.isPresent()) {
      response.put("err", "Email taken");
      return response;
    }

    String hashedPassword = passwordConfiguration.passwordEncoder().encode(password);

    User user = new User(username, email, hashedPassword, phoneNumber);
    userRepository.save(user);

    UUID userId = user.getId();

    String token = jwtConfiguration.createToken(userId, username);
    return Map.of("token", token);
  }
}
