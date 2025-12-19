package com.mimaja.job_finder_app.feature.user.manage.email.controller;

import com.mimaja.job_finder_app.feature.user.manage.email.service.UpdateEmailService;
import com.mimaja.job_finder_app.feature.user.manage.email.shared.requestDto.UpdateEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.email.shared.responseDto.UpdateEmailResponseDto;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/update/email")
public class UpdateEmailController {
    private final UpdateEmailService updateEmailService;

    @PatchMapping
    public ResponseDto<UpdateEmailResponseDto> updateEmail(
            @RequestBody UpdateEmailRequestDto reqBody,
            @AuthenticationPrincipal JwtPrincipal principal) {
        UpdateEmailResponseDto response = updateEmailService.updateEmail(reqBody, principal);

        return new ResponseDto<UpdateEmailResponseDto>(
                SuccessCode.RESOURCE_UPDATED, "Email updated", response);
    }
}
