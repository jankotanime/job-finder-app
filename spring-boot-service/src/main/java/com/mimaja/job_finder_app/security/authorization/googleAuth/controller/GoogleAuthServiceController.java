package com.mimaja.job_finder_app.security.authorization.googleAuth.controller;

import com.mimaja.job_finder_app.security.authorization.googleAuth.service.GoogleAuthServiceAndroid;
import com.mimaja.job_finder_app.security.authorization.googleAuth.service.GoogleAuthServiceIos;
import com.mimaja.job_finder_app.security.shared.dto.RequestGoogleAuthDto;
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
@RequestMapping("/auth/google-auth")
public class GoogleAuthServiceController {
    private final GoogleAuthServiceIos googleAuthServiceIos;
    private final GoogleAuthServiceAndroid googleAuthServiceAndroid;

    @PostMapping("/ios")
    public ResponseDto<ResponseTokenDto> googleAuthIosPostMapping(
            @RequestBody RequestGoogleAuthDto reqData) {
        ResponseTokenDto tokens = googleAuthServiceIos.tryToLoginViaGoogle(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Successfuly logged in", tokens);
    }

    @PostMapping("/android")
    public ResponseDto<ResponseTokenDto> googleAuthAndroidPostMapping(
            @RequestBody RequestGoogleAuthDto reqData) {
        ResponseTokenDto tokens = googleAuthServiceAndroid.tryToLoginViaGoogle(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Successfuly logged in", tokens);
    }
}
