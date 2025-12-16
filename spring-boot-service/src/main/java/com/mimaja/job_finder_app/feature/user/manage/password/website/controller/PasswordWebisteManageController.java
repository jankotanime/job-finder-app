package com.mimaja.job_finder_app.feature.user.manage.password.website.controller;

import com.mimaja.job_finder_app.feature.user.manage.password.website.service.PasswordWebsiteManageServiceDefault;
import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordUpdateByEmailDto;
import com.mimaja.job_finder_app.security.shared.dto.RequestPasswordUpdateEmailRequestDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponsePasswordUpdateEmailRequestDto;
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
            @RequestBody RequestPasswordUpdateByEmailDto reqData) {
        passwordWebsiteManageServiceDefault.updatePasswordByEmail(reqData);

        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED, "Password successfuly updated", null);
    }

    @PostMapping("/send-email")
    public ResponseDto<ResponsePasswordUpdateEmailRequestDto> sendEmailPutMapping(
            @RequestBody RequestPasswordUpdateEmailRequestDto reqData) {
        ResponsePasswordUpdateEmailRequestDto result =
                passwordWebsiteManageServiceDefault.sendEmailWithUpdatePasswordRequest(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Email successfuly sent", result);
    }
}
