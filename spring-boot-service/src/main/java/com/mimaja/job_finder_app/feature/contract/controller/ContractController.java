package com.mimaja.job_finder_app.feature.contract.controller;

import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUploadRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.response.ContractDto;
import com.mimaja.job_finder_app.feature.contract.service.ContractService;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contract")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseDto<ContractDto> uploadContract(
            @ModelAttribute ContractUploadRequestDto reqBody,
            @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        ContractDto response = contractService.uploadContract(reqBody, jwtPrincipal);

        return new ResponseDto<ContractDto>(
                SuccessCode.RESOURCE_CREATED, "Contract created", response);
    }
}
