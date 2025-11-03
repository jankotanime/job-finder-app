package com.mimaja.job_finder_app.security.manage.password.utils;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.shared.dto.ResponseResetTokenDto;
import com.mimaja.job_finder_app.security.tokens.resetTokens.service.ResetTokenServiceDefault;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PasswordWebsiteManager {
  private final UserRepository userRepository;
  private final ResetTokenServiceDefault respTokenServiceDefault;

  public User findUser(String loginData) {
    Optional<User> userOptional = userRepository.findByUsername(loginData);

    if (userOptional.isEmpty()) {
      userOptional = userRepository.findByEmail(loginData);
    }

    if (userOptional.isEmpty()) {
      try {
        int phoneNumber = Integer.parseInt(loginData);
        userOptional = userRepository.findByPhoneNumber(phoneNumber);
      } catch (NumberFormatException e) {
        throw new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA);
      }
    }

    if (userOptional.isEmpty()) {
      throw new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA);
    }

    return userOptional.get();
  }

  public void sendEmail(UUID userId) {
    ResponseResetTokenDto resetToken = respTokenServiceDefault.createToken(userId);
    // TODO: Email needs update
    String emailContent = "Hi"
      + "A request has been received to change the password for your ... account."
      + "To change password click the link below (you will be redirected to external website):"
      + "https::localhost/reset-password?token-id=" + resetToken.resetTokenId() + "&token=" + resetToken.resetToken()
      + "If you did not initiate this request, please contact is immediatelly at ...";

    System.out.println(emailContent);
  }
}
