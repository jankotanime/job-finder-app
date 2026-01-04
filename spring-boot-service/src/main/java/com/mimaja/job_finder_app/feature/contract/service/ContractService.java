package com.mimaja.job_finder_app.feature.contract.service;

import com.mimaja.job_finder_app.feature.contract.dto.request.ContractDevelopRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUpdateRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUploadRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.response.ContractDto;
import com.mimaja.job_finder_app.feature.job.dto.JobResponseDto;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;

public interface ContractService {
    ContractDto uploadContract(ContractUploadRequestDto requestDto, JwtPrincipal principal);

    ContractDto updateContract(ContractUpdateRequestDto requestDto, JwtPrincipal principal);

    JobResponseDto acceptContract(ContractDevelopRequestDto requestDto, JwtPrincipal principal);

    void declineContract(ContractDevelopRequestDto requestDto, JwtPrincipal principal);
}
