package com.mimaja.job_finder_app.feature.user.update.password.mobile.controller;

import com.mimaja.job_finder_app.feature.user.update.password.mobile.service.PasswordMobileManageService;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePasswordRequestDto;
import com.mimaja.job_finder_app.security.shared.dto.TokenResponseDto;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/password/mobile")
public class PasswordMobileManageController {
    private final PasswordMobileManageService passwordManageService;

    @PutMapping("/update")
    public ResponseDto<TokenResponseDto> updatePasswordPostMapping(
            @RequestBody UpdatePasswordRequestDto reqData,
            @AuthenticationPrincipal JwtPrincipal principal) {
        passwordManageService.updatePassword(reqData, principal);

        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED, "Password successfuly updated", null);
    }
}
