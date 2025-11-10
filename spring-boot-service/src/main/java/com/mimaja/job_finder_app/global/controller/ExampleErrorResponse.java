package com.mimaja.job_finder_app.global.controller;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("example-error")
public class ExampleErrorResponse {
    @GetMapping
    public void getError() {
        throw new BusinessException(BusinessExceptionReason.JOB_NOT_FOUND);
    }
}
