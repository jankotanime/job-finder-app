package com.mimaja.job_finder_app.feature.user.manage.userData.service;

import com.mimaja.job_finder_app.feature.user.manage.userData.shared.request.UpdateUserDataRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.userData.shared.response.UpdateUserDataResponseDto;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;

public interface UpdateUserDataService {
    UpdateUserDataResponseDto updateUserdata(
            UpdateUserDataRequestDto reqData, JwtPrincipal principal);
}
