package com.mimaja.job_finder_app.feature.contract.controller;

import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUpdateRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUploadRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.response.ContractDto;
import com.mimaja.job_finder_app.feature.contract.service.ContractService;
import com.mimaja.job_finder_app.feature.job.dto.JobResponseDto;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contract")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseDto<ContractDto> uploadContract(
            @Valid @ModelAttribute ContractUploadRequestDto reqBody,
            @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        ContractDto response = contractService.uploadContract(reqBody, jwtPrincipal);

        return new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Contract created", response);
    }

    @GetMapping("/{contractId}")
    ResponseDto<ContractDto> getContract(
            @PathVariable UUID contractId, @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        ContractDto response = contractService.getContract(contractId, jwtPrincipal);

        return new ResponseDto<>(SuccessCode.RESPONSE_SUCCESSFUL, "Contract found", response);
    }

    @GetMapping("/by-offer/{offerId}")
    ResponseDto<ContractDto> getContractByOfferId(
            @PathVariable UUID offerId, @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        ContractDto response = contractService.getContractByOfferId(offerId, jwtPrincipal);

        return new ResponseDto<>(SuccessCode.RESPONSE_SUCCESSFUL, "Contract found", response);
    }

    @DeleteMapping("/{contractId}")
    ResponseDto<ContractDto> deleteContract(
            @PathVariable UUID contractId, @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        contractService.deleteContract(contractId, jwtPrincipal);

        return new ResponseDto<>(SuccessCode.RESOURCE_DELETED, "Contract deleted", null);
    }

    @PutMapping(path = "/{contractId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseDto<ContractDto> updateContract(
            @PathVariable UUID contractId,
            @Valid @ModelAttribute ContractUpdateRequestDto reqBody,
            @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        ContractDto response = contractService.updateContract(contractId, reqBody, jwtPrincipal);

        return new ResponseDto<>(SuccessCode.RESOURCE_UPDATED, "Contract updated", response);
    }

    @PostMapping("/{contractId}/accept")
    ResponseDto<JobResponseDto> acceptContract(
            @PathVariable UUID contractId, @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        JobResponseDto response = contractService.acceptContract(contractId, jwtPrincipal);

        return new ResponseDto<>(
                SuccessCode.RESOURCE_CREATED, "Contract accepted and job created", response);
    }

    @PatchMapping("/{contractId}/decline")
    ResponseDto<Void> declineContract(
            @PathVariable UUID contractId, @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        contractService.declineContract(contractId, jwtPrincipal);

        return new ResponseDto<>(SuccessCode.RESOURCE_UPDATED, "Contract declined", null);
    }
}
