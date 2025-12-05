package com.mimaja.job_finder_app.feature.user.profileCompletionForm.service;

import org.springframework.stereotype.Service;

import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormRequestDto;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormResponseDto;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FormServiceDefault implements FormService {
  private final UserRepository userRepository;
  private final JwtConfiguration jwtConfiguration;

  @Override
  public ProfileCompletionFormResponseDto sendForm(ProfileCompletionFormRequestDto reqData, JwtPrincipal principal) {
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

    String accessToken = jwtConfiguration.createToken(user);
    return new ProfileCompletionFormResponseDto(accessToken);
  }
}
