package com.mimaja.job_finder_app.feature.user.profileCompletionForm.service;

import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormRequestDto;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormResponseDto;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.token.accessToken.dto.response.CreateAccessTokenResponseDto;
import com.mimaja.job_finder_app.security.token.accessToken.service.AccessTokenService;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FormServiceDefault implements FormService {
    private final UserRepository userRepository;
    private final AccessTokenService accessTokenService;

    @Override
    public ProfileCompletionFormResponseDto sendForm(
            ProfileCompletionFormRequestDto reqData, JwtPrincipal principal) {
        User user = principal.user();

        String firstName = reqData.firstName();
        String lastName = reqData.lastName();
        String profileDescription = reqData.profileDescription();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setProfileDescription(profileDescription);

        System.out.println(user.toString());

        System.out.println(firstName);
        System.out.println(lastName);
        System.out.println(profileDescription);

        userRepository.save(user);

        CreateAccessTokenResponseDto accessTokenDto = accessTokenService.createToken(user);
        return new ProfileCompletionFormResponseDto(accessTokenDto.accessToken());
    }
}
