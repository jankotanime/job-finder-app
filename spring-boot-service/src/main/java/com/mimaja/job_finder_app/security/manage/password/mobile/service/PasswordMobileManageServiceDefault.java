package com.mimaja.job_finder_app.security.manage.password.mobile.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.users.model.User;
import com.mimaja.job_finder_app.feature.users.repository.UserRepository;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.manage.password.utils.PasswordManageDataManager;
import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordDto;
import com.mimaja.job_finder_app.security.tokens.jwt.utils.JwtAuthenticationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordMobileManageServiceDefault implements PasswordMobileManageService {
    private final UserRepository userRepository;
    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final PasswordConfiguration passwordConfiguration;
    private final PasswordManageDataManager passwordManageDataManager;

    @Override
    public void updatePassword(RequestPasswordDto reqData) {
        String password = reqData.password();
        String newPassword = reqData.newPassword();

        User user = jwtAuthenticationManager.getUserFromAuthentication();

        if (!passwordConfiguration.verifyPassword(password, user.getPasswordHash())) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PASSWORD);
        }

        passwordManageDataManager.checkDataPatterns(newPassword);

        String newPasswordHash = passwordConfiguration.encodePassword(newPassword);

        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);
    }
}
