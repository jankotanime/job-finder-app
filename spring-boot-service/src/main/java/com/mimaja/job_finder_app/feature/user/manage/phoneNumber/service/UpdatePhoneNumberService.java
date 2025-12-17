package com.mimaja.job_finder_app.feature.user.manage.phoneNumber.service;

import com.mimaja.job_finder_app.feature.user.manage.phoneNumber.shared.requestDto.UpdatePhoneNumberRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.phoneNumber.shared.responseDto.UpdatePhoneNumberResponseDto;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;

public interface UpdatePhoneNumberService {
    UpdatePhoneNumberResponseDto updatePhoneNumber(
            UpdatePhoneNumberRequestDto reqData, JwtPrincipal principal);
}
