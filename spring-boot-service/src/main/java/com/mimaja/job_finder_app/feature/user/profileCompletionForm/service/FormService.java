package com.mimaja.job_finder_app.feature.user.profileCompletionForm.service;

import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormRequestDto;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormResponseDto;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;

public interface FormService {
  public ProfileCompletionFormResponseDto sendForm(ProfileCompletionFormRequestDto reqData, JwtPrincipal principal);
}
