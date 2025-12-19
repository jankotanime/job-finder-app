package com.mimaja.job_finder_app.global.controller;

import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class Me {
    @GetMapping
    public ResponseDto<String> getMe(@AuthenticationPrincipal JwtPrincipal principal) {
        return new ResponseDto<>(SuccessCode.RESPONSE_SUCCESSFUL, "Me!", principal.username());
    }
}
