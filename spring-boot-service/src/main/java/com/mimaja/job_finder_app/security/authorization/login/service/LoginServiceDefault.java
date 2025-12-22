package com.mimaja.job_finder_app.security.authorization.login.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.security.authorization.login.dto.request.LoginRequestDto;
import com.mimaja.job_finder_app.security.authorization.login.utils.DefaultLoginValidation;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import com.mimaja.job_finder_app.security.token.refreshToken.service.RefreshTokenServiceDefault;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceDefault implements LoginService {
    private final RefreshTokenServiceDefault refreshTokenServiceDefault;
    private final PasswordConfiguration passwordConfiguration;
    private final DefaultLoginValidation defaultLoginValidation;

    @Override
    public ResponseTokenDto tryToLogin(LoginRequestDto reqData) {
        String loginData = reqData.loginData();
        String password = reqData.password();

        User user = defaultLoginValidation.userValidation(loginData);

        if (user.getPasswordHash() == null) {
            throw new BusinessException(BusinessExceptionReason.LACK_OF_PASSWORD);
        }

        if (!passwordConfiguration.verifyPassword(password, user.getPasswordHash())) {
            throw new BusinessException(BusinessExceptionReason.WRONG_LOGIN_DATA);
        }

        ResponseTokenDto tokens = refreshTokenServiceDefault.createTokensSet(user);
        return tokens;
    }
}
