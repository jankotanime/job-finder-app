package com.mimaja.job_finder_app.feature.user.manage.phoneNumber.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.manage.phoneNumber.shared.requestDto.UpdatePhoneNumberRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.phoneNumber.shared.responseDto.UpdatePhoneNumberResponseDto;
import com.mimaja.job_finder_app.feature.user.manage.utils.CheckDataValidity;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UpdatePhoneNumberServiceDefault implements UpdatePhoneNumberService {
    private final CheckDataValidity checkDataValidity;
    private final PasswordConfiguration passwordConfiguration;
    private final UserRepository userRepository;
    private final JwtConfiguration jwtConfiguration;

    @Override
    public UpdatePhoneNumberResponseDto updatePhoneNumber(
            UpdatePhoneNumberRequestDto reqData, JwtPrincipal principal) {
        int newPhoneNumber = reqData.newPhoneNumber();
        User user = principal.user();

        checkDataValidity.checkPhoneNumber(user.getId(), newPhoneNumber);

        if (!passwordConfiguration.verifyPassword(reqData.password(), user.getPasswordHash())) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PASSWORD);
        }

        user.setPhoneNumber(newPhoneNumber);

        userRepository.save(user);

        String accessToken = jwtConfiguration.createToken(user);

        return new UpdatePhoneNumberResponseDto(accessToken);
    }
}
