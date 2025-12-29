package com.mimaja.job_finder_app.security.authorization.login.controller;

import com.mimaja.job_finder_app.security.authorization.login.dto.request.LoginRequestDto;
import com.mimaja.job_finder_app.security.authorization.login.service.LoginService;
import com.mimaja.job_finder_app.security.shared.dto.TokenResponseDto;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/login")
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    public ResponseDto<TokenResponseDto> loginPostMapping(@RequestBody LoginRequestDto reqData) {
        TokenResponseDto tokens = loginService.tryToLogin(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Successfuly logged in", tokens);
    }
}
