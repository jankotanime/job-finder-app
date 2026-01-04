package com.mimaja.job_finder_app.feature.contract.service;

import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUpdateRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUploadRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.response.ContractDto;
import com.mimaja.job_finder_app.feature.job.dto.JobResponseDto;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.util.UUID;

public interface ContractService {
    ContractDto uploadContract(ContractUploadRequestDto requestDto, JwtPrincipal principal);

    ContractDto updateContract(
            UUID contractId, ContractUpdateRequestDto requestDto, JwtPrincipal principal);

    JobResponseDto acceptContract(UUID contractId, JwtPrincipal principal);

    void declineContract(UUID contractId, JwtPrincipal principal);

    void deleteContract(UUID contractId, JwtPrincipal principal);

    ContractDto getContract(UUID contractId, JwtPrincipal principal);
}
