package com.mimaja.job_finder_app.feature.user.manage.email.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.manage.email.shared.requestDto.UpdateEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.email.shared.responseDto.UpdateEmailResponseDto;
import com.mimaja.job_finder_app.feature.user.manage.utils.CheckDataValidity;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateEmailServiceDefault implements UpdateEmailService {
    private final CheckDataValidity checkDataValidity;
    private final PasswordConfiguration passwordConfiguration;
    private final UserRepository userRepository;
    private final JwtConfiguration jwtConfiguration;

    @Override
    public UpdateEmailResponseDto updateEmail(
            UpdateEmailRequestDto reqData, JwtPrincipal principal) {
        String newEmail = reqData.newEmail();
        User user = principal.user();

        checkDataValidity.checkEmail(newEmail);

        if (!passwordConfiguration.verifyPassword(reqData.password(), user.getPasswordHash())) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PASSWORD);
        }

        user.setEmail(newEmail);

        userRepository.save(user);

        String accessToken = jwtConfiguration.createToken(user);

        return new UpdateEmailResponseDto(accessToken);
    }
}
