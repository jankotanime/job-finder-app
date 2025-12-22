package com.mimaja.job_finder_app.feature.user.update.service;

import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePhoneNumberRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateUserDataRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateEmailResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdatePhoneNumberResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateUserDataResponseDto;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;

public interface UserUpdateService {
    UpdateUserDataResponseDto updateUserdata(
            UpdateUserDataRequestDto reqData, JwtPrincipal principal);

    UpdateEmailResponseDto updateEmail(UpdateEmailRequestDto reqData, JwtPrincipal principal);

    UpdatePhoneNumberResponseDto updatePhoneNumber(
            UpdatePhoneNumberRequestDto reqData, JwtPrincipal principal);
}
