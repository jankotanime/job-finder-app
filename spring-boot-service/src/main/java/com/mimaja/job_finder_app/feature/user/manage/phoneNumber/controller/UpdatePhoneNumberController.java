package com.mimaja.job_finder_app.feature.user.manage.phoneNumber.controller;

import com.mimaja.job_finder_app.feature.user.manage.phoneNumber.service.UpdatePhoneNumberService;
import com.mimaja.job_finder_app.feature.user.manage.phoneNumber.shared.requestDto.UpdatePhoneNumberRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.phoneNumber.shared.responseDto.UpdatePhoneNumberResponseDto;
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
@RequestMapping("/update/phone-number")
@RequiredArgsConstructor
public class UpdatePhoneNumberController {
    private final UpdatePhoneNumberService updatePhoneNumberService;

    @PatchMapping
    public ResponseDto<UpdatePhoneNumberResponseDto> updatePhoneNumber(
            @RequestBody UpdatePhoneNumberRequestDto reqData,
            @AuthenticationPrincipal JwtPrincipal principal) {
        UpdatePhoneNumberResponseDto response =
                updatePhoneNumberService.updatePhoneNumber(reqData, principal);

        return new ResponseDto<UpdatePhoneNumberResponseDto>(
                SuccessCode.RESOURCE_UPDATED, "Phone number successfuly updated", response);
    }
}
