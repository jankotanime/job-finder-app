package com.mimaja.job_finder_app.feature.user.manage.userData.controller;

import com.mimaja.job_finder_app.feature.user.manage.userData.service.UpdateUserDataServiceDefault;
import com.mimaja.job_finder_app.feature.user.manage.userData.shared.request.UpdateUserDataRequestDto;
import com.mimaja.job_finder_app.feature.user.manage.userData.shared.response.UpdateUserDataResponseDto;
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
@RequestMapping("/manage/user-data")
@RequiredArgsConstructor
public class UpdateUserDataController {
    private final UpdateUserDataServiceDefault updateUserDataServiceDefault;

    @PatchMapping
    ResponseDto<UpdateUserDataResponseDto> updateUserdata(
            @RequestBody UpdateUserDataRequestDto reqData,
            @AuthenticationPrincipal JwtPrincipal principal) {
        UpdateUserDataResponseDto response =
                updateUserDataServiceDefault.updateUserdata(reqData, principal);

        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED, "User data successfuly updated", response);
    }
}
