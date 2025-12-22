package com.mimaja.job_finder_app.feature.user.update.password.website.controller;

import com.mimaja.job_finder_app.feature.user.update.password.website.service.PasswordWebsiteManageServiceDefault;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.SendEmailToUpdatePasswordRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePasswordByEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.SendEmailToUpdatePasswordResponseDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/password/website")
public class PasswordWebisteManageController {
    private final PasswordWebsiteManageServiceDefault passwordWebsiteManageServiceDefault;

    @PutMapping("/update")
    public ResponseDto<ResponseTokenDto> updatePasswordPostMapping(
            @RequestBody UpdatePasswordByEmailRequestDto reqData) {
        passwordWebsiteManageServiceDefault.updatePasswordByEmail(reqData);

        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED, "Password successfuly updated", null);
    }

    @PostMapping("/send-email")
    public ResponseDto<SendEmailToUpdatePasswordResponseDto> sendEmailPutMapping(
            @RequestBody SendEmailToUpdatePasswordRequestDto reqData) {
        SendEmailToUpdatePasswordResponseDto result =
                passwordWebsiteManageServiceDefault.sendEmailWithUpdatePasswordRequest(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Email successfuly sent", result);
    }
}
