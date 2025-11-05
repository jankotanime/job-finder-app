package com.mimaja.job_finder_app.security.authorization.register.controller;

import com.mimaja.job_finder_app.security.authorization.register.service.RegisterServiceDefault;
import com.mimaja.job_finder_app.security.shared.dto.RequestRegisterDto;
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
@RequestMapping("/auth/register")
public class RegisterController {
    private final RegisterServiceDefault registerService;

    @PostMapping
    public ResponseDto<ResponseTokenDto> registerPostMapping(
            @RequestBody RequestRegisterDto reqData) {
        ResponseTokenDto tokens = registerService.tryToRegister(reqData);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Successfully registered", tokens);
    }
}
