package com.mimaja.job_finder_app.security.authorization.googleAuth.controller;

import com.mimaja.job_finder_app.security.authorization.googleAuth.service.GoogleAuthServiceIos;
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
@RequestMapping("/auth/google-auth/ios")
public class GoogleAuthIosController {
    private final GoogleAuthServiceIos googleAuthServiceIos;

    @PostMapping("/login")
    public ResponseDto<ResponseGoogleAuthLoginDto> googleAuthIosLogin(
            @RequestBody RequestGoogleAuthLoginDto reqData) {
        ResponseGoogleAuthLoginDto response = googleAuthServiceIos.tryToLoginViaGoogle(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Successfuly logged in", response);
    }

    @PostMapping("/check-user-existence")
    public ResponseDto<ResponseGoogleIdExistDto> googleAuthIosCheckExistence(
            @RequestBody RequestGoogleAuthLoginDto reqData) {
        ResponseGoogleIdExistDto response = googleAuthServiceIos.checkUserExistence(reqData);

        return new ResponseDto<>(SuccessCode.RESPONSE_SUCCESSFUL, "User existence found", response);
    }

    @PostMapping("/register")
    public ResponseDto<ResponseTokenDto> googleAuthIosRegister(
            @RequestBody RequestGoogleAuthRegisterDto reqData) {
        ResponseTokenDto tokens = googleAuthServiceIos.tryToRegisterViaGoogle(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Succesfuly registered", tokens);
    }
}
