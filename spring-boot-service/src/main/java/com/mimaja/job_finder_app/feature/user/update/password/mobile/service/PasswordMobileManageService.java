package com.mimaja.job_finder_app.feature.user.update.password.mobile.service;

import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePasswordRequestDto;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;

public interface PasswordMobileManageService {
    void updatePassword(UpdatePasswordRequestDto reqData, JwtPrincipal principal);
}
