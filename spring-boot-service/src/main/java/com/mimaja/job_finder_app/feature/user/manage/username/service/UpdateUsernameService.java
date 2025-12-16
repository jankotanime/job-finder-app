package com.mimaja.job_finder_app.feature.user.manage.username.service;

import com.mimaja.job_finder_app.feature.user.manage.username.shared.UpdateUsernameRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.username.shared.UpdateUsernameResponseDto;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;

public interface UpdateUsernameService {
    UpdateUsernameResponseDto updateUsername(
            UpdateUsernameRequestDto reqData, JwtPrincipal principal);
}
