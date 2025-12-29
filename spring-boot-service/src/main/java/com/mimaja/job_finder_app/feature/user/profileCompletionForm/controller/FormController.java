package com.mimaja.job_finder_app.feature.user.profileCompletionForm.controller;

import com.mimaja.job_finder_app.feature.user.profileCompletionForm.service.FormServiceDefault;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormRequestDto;
import com.mimaja.job_finder_app.feature.user.profileCompletionForm.shared.ProfileCompletionFormResponseDto;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profile-completion-form")
public class FormController {
    private final FormServiceDefault formServiceDefault;

    @PostMapping
    public ResponseDto<ProfileCompletionFormResponseDto> sendForm(
            @RequestBody ProfileCompletionFormRequestDto reqData,
            @AuthenticationPrincipal JwtPrincipal principal) {
        ProfileCompletionFormResponseDto response = formServiceDefault.sendForm(reqData, principal);

        return new ResponseDto<>(SuccessCode.RESOURCE_UPDATED, "Profile completed", response);
    }
}
