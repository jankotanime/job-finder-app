package com.mimaja.job_finder_app.global.controller;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("example-error")
public class ExampleErrorResponse {
    @GetMapping
    public ResponseDto<String> getError() {
        if (true) {
            throw new BusinessException(BusinessExceptionReason.JOB_NOT_FOUND);
        }
        return new ResponseDto<>(SuccessCode.RESPONSE_SUCCESSFUL, "Error", null);
    }
}
