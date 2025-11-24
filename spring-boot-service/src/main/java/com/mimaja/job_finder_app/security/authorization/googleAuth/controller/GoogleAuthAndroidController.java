package com.mimaja.job_finder_app.security.authorization.googleAuth.controller;

import com.mimaja.job_finder_app.security.authorization.googleAuth.service.GoogleAuthServiceAndroid;
import com.mimaja.job_finder_app.security.shared.dto.RequestGoogleAuthCheckExistenceDto;
import com.mimaja.job_finder_app.security.shared.dto.RequestGoogleAuthLoginDto;
import com.mimaja.job_finder_app.security.shared.dto.RequestGoogleAuthRegisterDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseGoogleAuthLoginDto;
import com.mimaja.job_finder_app.security.shared.dto.ResponseGoogleIdExistDto;
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
@RequestMapping("/auth/google-auth/android")
public class GoogleAuthAndroidController {
    private final GoogleAuthServiceAndroid googleAuthServiceAndroid;

    @PostMapping("/login")
    public ResponseDto<ResponseGoogleAuthLoginDto> googleAuthAndroidLogin(
            @RequestBody RequestGoogleAuthLoginDto reqData) {
        ResponseGoogleAuthLoginDto response = googleAuthServiceAndroid.tryToLoginViaGoogle(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Successfuly logged in", response);
    }

    @PostMapping("/check-user-existence")
    public ResponseDto<ResponseGoogleIdExistDto> googleAuthAndroidCheckExistence(
            @RequestBody RequestGoogleAuthCheckExistenceDto reqData) {
        ResponseGoogleIdExistDto response = googleAuthServiceAndroid.checkUserExistence(reqData);

        return new ResponseDto<>(SuccessCode.RESPONSE_SUCCESSFUL, "User existence found", response);
    }

    @PostMapping("/register")
    public ResponseDto<ResponseTokenDto> googleAuthAndroidRegister(
            @RequestBody RequestGoogleAuthRegisterDto reqData) {
        ResponseTokenDto tokens = googleAuthServiceAndroid.tryToRegisterViaGoogle(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Succesfuly registered", tokens);
    }
}
