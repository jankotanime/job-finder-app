package com.mimaja.job_finder_app.feature.user.update.controller;

import com.mimaja.job_finder_app.feature.user.update.service.UserUpdateService;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePhoneNumberRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateUserDataRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateEmailResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdatePhoneNumberResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateUserDataResponseDto;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/update")
@RequiredArgsConstructor
public class UserUpdateController {
    private final UserUpdateService userUpdateService;

    @PatchMapping("/email")
    public ResponseDto<UpdateEmailResponseDto> updateEmail(
            @RequestBody UpdateEmailRequestDto reqBody,
            @AuthenticationPrincipal JwtPrincipal principal) {
        UpdateEmailResponseDto response = userUpdateService.updateEmail(reqBody, principal);

        return new ResponseDto<UpdateEmailResponseDto>(
                SuccessCode.RESOURCE_UPDATED, "Email updated", response);
    }

    @PatchMapping("/phone-number")
    public ResponseDto<UpdatePhoneNumberResponseDto> updatePhoneNumber(
            @RequestBody UpdatePhoneNumberRequestDto reqData,
            @AuthenticationPrincipal JwtPrincipal principal) {
        UpdatePhoneNumberResponseDto response =
                userUpdateService.updatePhoneNumber(reqData, principal);

        return new ResponseDto<UpdatePhoneNumberResponseDto>(
                SuccessCode.RESOURCE_UPDATED, "Phone number successfuly updated", response);
    }

    @PatchMapping("/user-data")
    ResponseDto<UpdateUserDataResponseDto> updateUserdata(
            @RequestBody UpdateUserDataRequestDto reqData,
            @AuthenticationPrincipal JwtPrincipal principal) {
        UpdateUserDataResponseDto response = userUpdateService.updateUserdata(reqData, principal);

        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED, "User data successfuly updated", response);
    }
}
