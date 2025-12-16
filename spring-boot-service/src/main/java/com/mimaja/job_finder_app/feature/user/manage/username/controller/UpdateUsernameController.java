package com.mimaja.job_finder_app.feature.user.manage.username.controller;

import com.mimaja.job_finder_app.feature.user.manage.username.service.UpdateUsernameServiceDefault;
import com.mimaja.job_finder_app.feature.user.manage.username.shared.UpdateUsernameRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.username.shared.UpdateUsernameResponseDto;
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
@RequestMapping("/manage/username")
@RequiredArgsConstructor
public class UpdateUsernameController {
    private final UpdateUsernameServiceDefault updateUsernameServiceDefault;

    @PatchMapping
    ResponseDto<UpdateUsernameResponseDto> updatePassword(
            @RequestBody UpdateUsernameRequestDto reqData,
            @AuthenticationPrincipal JwtPrincipal principal) {
        UpdateUsernameResponseDto response =
                updateUsernameServiceDefault.updateUsername(reqData, principal);

        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED, "Username successfuly updated", response);
    }
}
