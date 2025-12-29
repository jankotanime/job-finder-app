package com.mimaja.job_finder_app.feature.user.update.password.website.service;

import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.SendEmailToUpdatePasswordRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePasswordByEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.SendEmailToUpdatePasswordResponseDto;

public interface PasswordWebsiteManageService {
    SendEmailToUpdatePasswordResponseDto sendEmailWithUpdatePasswordRequest(
            SendEmailToUpdatePasswordRequestDto reqData);

    void updatePasswordByEmail(UpdatePasswordByEmailRequestDto reqData);
}
