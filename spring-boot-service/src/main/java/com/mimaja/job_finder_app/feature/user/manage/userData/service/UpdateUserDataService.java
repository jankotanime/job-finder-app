package com.mimaja.job_finder_app.feature.user.manage.userData.service;

import com.mimaja.job_finder_app.feature.user.manage.userData.shared.requestDto.UpdateUserDataRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.userData.shared.responseDto.UpdateUserDataResponseDto;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;

public interface UpdateUserDataService {
    UpdateUserDataResponseDto updateUserdata(
            UpdateUserDataRequestDto reqData, JwtPrincipal principal);
}
