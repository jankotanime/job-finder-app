package com.mimaja.job_finder_app.feature.user.update.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePhoneNumberRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateUserDataRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateEmailResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdatePhoneNumberResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateUserDataResponseDto;
import com.mimaja.job_finder_app.feature.user.update.utils.CheckDataValidity;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserUpdateServiceDefault implements UserUpdateService {
    private final CheckDataValidity checkDataValidity;
    private final PasswordConfiguration passwordConfiguration;
    private final UserRepository userRepository;
    private final JwtConfiguration jwtConfiguration;

    @Override
    public UpdateUserDataResponseDto updateUserdata(
            UpdateUserDataRequestDto reqData, JwtPrincipal principal) {
        String newUsername = reqData.newUsername();
        String newFirstName = reqData.newFirstName();
        String newLastName = reqData.newLastName();
        String newProfileDescription = reqData.newProfileDescription();

        User user = principal.user();

        if (!passwordConfiguration.verifyPassword(reqData.password(), user.getPasswordHash())) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PASSWORD);
        }

        checkDataValidity.checkUsername(user.getId(), newUsername);
        checkDataValidity.checkRestData(newFirstName);
        checkDataValidity.checkRestData(newLastName);
        checkDataValidity.checkRestData(newProfileDescription);

        user.setUsername(newUsername);
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setProfileDescription(newProfileDescription);

        userRepository.save(user);

        String accessToken = jwtConfiguration.createToken(user);

        return new UpdateUserDataResponseDto(accessToken);
    }

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

    @Override
    public UpdateEmailResponseDto updateEmail(
            UpdateEmailRequestDto reqData, JwtPrincipal principal) {
        String newEmail = reqData.newEmail();
        User user = principal.user();

        checkDataValidity.checkEmail(user.getId(), newEmail);

        if (!passwordConfiguration.verifyPassword(reqData.password(), user.getPasswordHash())) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PASSWORD);
        }

        user.setEmail(newEmail);

        userRepository.save(user);

        String accessToken = jwtConfiguration.createToken(user);

        return new UpdateEmailResponseDto(accessToken);
    }
}
