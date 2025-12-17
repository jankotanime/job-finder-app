package com.mimaja.job_finder_app.feature.user.manage.userData.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.manage.userData.shared.request.UpdateUserDataRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.userData.shared.response.UpdateUserDataResponseDto;
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
public class UpdateUserDataServiceDefault implements UpdateUserDataService {
    private final CheckDataValidity checkDataValidity;
    private final PasswordConfiguration passwordConfiguration;
    private final UserRepository userRepository;
    private JwtConfiguration jwtConfiguration;

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
}
