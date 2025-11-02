package com.mimaja.job_finder_app.security.password.service;

import org.springframework.stereotype.Service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.password.utils.DefaultPasswordManageDataManager;
import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordDto;
import com.mimaja.job_finder_app.security.tokens.jwt.utils.JwtAuthenticationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordManageServiceDefault implements PasswordManageService {
  private final UserRepository userRepository;
  private final JwtAuthenticationManager jwtAuthenticationManager;
  private final PasswordConfiguration passwordConfiguration;
  private final DefaultPasswordManageDataManager defaultPasswordManageDataManager;

  @Override
  public void changePassword(RequestPasswordDto reqData) {
    String password = reqData.password();
    String newPassword = reqData.newPassword();

    User user = jwtAuthenticationManager.getUserFromAuthentication();

    if (!passwordConfiguration.verifyPassword(password, user.getPasswordHash())) {
      throw new BusinessException(BusinessExceptionReason.WRONG_PASSWORD);
    }

    defaultPasswordManageDataManager.checkDataPatterns(newPassword);

    String newPasswordHash = passwordConfiguration.encodePassword(newPassword);

    user.setPasswordHash(newPasswordHash);
    userRepository.save(user);
  }
}
