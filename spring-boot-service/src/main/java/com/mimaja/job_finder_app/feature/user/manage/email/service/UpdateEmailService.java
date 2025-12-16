package com.mimaja.job_finder_app.feature.user.manage.email.service;

import com.mimaja.job_finder_app.feature.user.manage.email.shared.requestDto.UpdateEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.email.shared.responseDto.UpdateEmailResponseDto;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;

public interface UpdateEmailService {
    UpdateEmailResponseDto updateEmail(UpdateEmailRequestDto reqData, JwtPrincipal principal);
}
