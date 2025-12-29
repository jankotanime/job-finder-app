package com.mimaja.job_finder_app.security.authorization.register.service;

import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.authorization.register.dto.request.RegisterRequestDto;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.shared.dto.TokenResponseDto;
import com.mimaja.job_finder_app.security.shared.utils.RegisterDataManager;
import com.mimaja.job_finder_app.security.token.refreshToken.service.RefreshTokenServiceDefault;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterServiceDefault implements RegisterService {
    private final PasswordConfiguration passwordConfiguration;
    private final UserRepository userRepository;
    private final RefreshTokenServiceDefault refreshTokenServiceDefault;
    private final RegisterDataManager registerDataManager;

    @Override
    public TokenResponseDto tryToRegister(RegisterRequestDto reqData) {
        String username = reqData.username();
        String email = reqData.email();
        String password = reqData.password();
        int phoneNumber = reqData.phoneNumber();

        registerDataManager.checkRegisterDataDefault(username, email, phoneNumber, password);

        String hashedPassword = passwordConfiguration.passwordEncoder().encode(password);

        User user = new User(username, email, hashedPassword, null, phoneNumber);
        userRepository.save(user);

        TokenResponseDto tokens = refreshTokenServiceDefault.createTokensSet(user);

        return tokens;
    }
}
