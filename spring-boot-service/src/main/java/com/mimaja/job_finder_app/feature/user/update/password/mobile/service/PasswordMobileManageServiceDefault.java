package com.mimaja.job_finder_app.feature.user.update.password.mobile.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.feature.user.update.password.utils.PasswordManageDataManager;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePasswordRequestDto;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordMobileManageServiceDefault implements PasswordMobileManageService {
    private final UserRepository userRepository;
    private final PasswordConfiguration passwordConfiguration;
    private final PasswordManageDataManager passwordManageDataManager;

    @Override
    public void updatePassword(UpdatePasswordRequestDto reqData, JwtPrincipal principal) {
        String password = reqData.password();
        String newPassword = reqData.newPassword();

        User user = principal.user();

        if (!passwordConfiguration.verifyPassword(password, user.getPasswordHash())) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PASSWORD);
        }

        passwordManageDataManager.checkDataPatterns(newPassword);

        String newPasswordHash = passwordConfiguration.encodePassword(newPassword);

        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);
    }
}
