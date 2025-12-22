package com.mimaja.job_finder_app.security.token.refreshToken.controller;

import com.mimaja.job_finder_app.security.shared.dto.ResponseTokenDto;
import com.mimaja.job_finder_app.security.token.refreshToken.dto.request.RequestRefreshTokenRotateDto;
import com.mimaja.job_finder_app.security.token.refreshToken.service.RefreshTokenServiceDefault;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/refresh-token")
public class RefreshTokenController {
    private final RefreshTokenServiceDefault refreshTokenServiceDefault;

    @PostMapping("/rotate")
    public ResponseDto<ResponseTokenDto> rotateToken(
            @RequestBody RequestRefreshTokenRotateDto reqData) {
        ResponseTokenDto tokens = refreshTokenServiceDefault.rotateToken(reqData);

        ResponseDto<ResponseTokenDto> response =
                new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Successfully refreshed", tokens);

        return response;
    }
}
