package com.mimaja.job_finder_app.security.authorization.googleAuth.controller;

import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthCheckExistenceRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthLoginRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.request.GoogleAuthRegisterRequestDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.response.GoogleAuthLoginResponseDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.dto.response.GoogleIdExistResponseDto;
import com.mimaja.job_finder_app.security.authorization.googleAuth.service.GoogleAuthServiceDefault;
import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/google-auth/ios")
public class GoogleAuthController {
    private final GoogleAuthServiceDefault googleAuthServiceDefault;

    @PostMapping("/login")
    public ResponseDto<GoogleAuthLoginResponseDto> googleAuthIosLogin(
            @RequestBody GoogleAuthLoginRequestDto reqData) {
        GoogleAuthLoginResponseDto response = googleAuthServiceDefault.tryToLoginViaGoogle(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Successfuly logged in", response);
    }

    @PostMapping("/check-user-existence")
    public ResponseDto<GoogleIdExistResponseDto> googleAuthIosCheckExistence(
            @RequestBody GoogleAuthCheckExistenceRequestDto reqData) {
        GoogleIdExistResponseDto response = googleAuthServiceDefault.checkUserExistence(reqData);

        return new ResponseDto<>(SuccessCode.RESPONSE_SUCCESSFUL, "User existence found", response);
    }

    @PostMapping("/register")
    public ResponseDto<ResponseTokenDto> googleAuthIosRegister(
            @RequestBody GoogleAuthRegisterRequestDto reqData) {
        ResponseTokenDto tokens = googleAuthServiceDefault.tryToRegisterViaGoogle(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Succesfuly registered", tokens);
    }
}
