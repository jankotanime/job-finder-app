package com.mimaja.job_finder_app.feature.user.manage.username.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.manage.username.shared.UpdateUsernameRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.username.shared.UpdateUsernameResponseDto;
import com.mimaja.job_finder_app.feature.user.manage.utils.CheckDataValidity;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UpdateUsernameServiceDefault implements UpdateUsernameService {
    private final CheckDataValidity checkDataValidity;
    private final PasswordConfiguration passwordConfiguration;
    private final UserRepository userRepository;
    private JwtConfiguration jwtConfiguration;

    @Override
    public UpdateUsernameResponseDto updateUsername(
            UpdateUsernameRequestDto reqData, JwtPrincipal principal) {
        String newUsername = reqData.newUsername();
        User user = principal.user();

        checkDataValidity.checkUsername(newUsername);

        if (!passwordConfiguration.verifyPassword(reqData.password(), user.getPasswordHash())) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PASSWORD);
        }

        user.setUsername(newUsername);

        userRepository.save(user);

        String accessToken = jwtConfiguration.createToken(user);

        return new UpdateUsernameResponseDto(accessToken);
    }
}
