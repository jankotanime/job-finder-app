package com.mimaja.job_finder_app.feature.user.update.password.website.service;

import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.feature.user.update.password.utils.PasswordManageDataManager;
import com.mimaja.job_finder_app.feature.user.update.password.utils.PasswordWebsiteManager;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordUpdateByEmailDto;
import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordUpdateEmailRequestDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponsePasswordUpdateEmailRequestDto;
import com.mimaja.job_finder_app.security.tokens.resetTokens.service.ResetTokenServiceDefault;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class PasswordWebsiteManageServiceDefault implements PasswordWebsiteManageService {
    private final PasswordWebsiteManager passwordWebsiteManager;
    private final ResetTokenServiceDefault resetTokenServiceDefault;
    private final PasswordManageDataManager passwordManageDataManager;
    private final PasswordConfiguration passwordConfiguration;
    private final UserRepository userRepository;

    @Override
    public ResponsePasswordUpdateEmailRequestDto sendEmailWithUpdatePasswordRequest(
            RequestPasswordUpdateEmailRequestDto reqData) {
        String loginData = reqData.loginData();
        User user = passwordWebsiteManager.findUser(loginData);

        passwordWebsiteManager.sendEmail(user.getId());

        String email = user.getEmail();
        String[] emailSplitted = email.split("@");
        String modifiedEmail = email.charAt(0) + "***@" + emailSplitted[emailSplitted.length - 1];

        ResponsePasswordUpdateEmailRequestDto response =
                new ResponsePasswordUpdateEmailRequestDto(modifiedEmail);

        return response;
    }

    @Override
    public void updatePasswordByEmail(RequestPasswordUpdateByEmailDto reqData) {
        String newPassword = reqData.password();
        String token = reqData.token();
        String tokenId = reqData.tokenId();

        User user = resetTokenServiceDefault.validateToken(token, tokenId);

        passwordManageDataManager.checkDataPatterns(newPassword);

        String newPasswordHash = passwordConfiguration.encodePassword(newPassword);

        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);

        resetTokenServiceDefault.deleteToken(tokenId);
    }
}
