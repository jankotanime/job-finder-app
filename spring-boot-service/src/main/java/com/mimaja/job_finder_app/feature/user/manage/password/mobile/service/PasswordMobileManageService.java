package com.mimaja.job_finder_app.feature.user.manage.password.mobile.service;

import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordDto;

public interface PasswordMobileManageService {
    void updatePassword(RequestPasswordDto reqData);
}
