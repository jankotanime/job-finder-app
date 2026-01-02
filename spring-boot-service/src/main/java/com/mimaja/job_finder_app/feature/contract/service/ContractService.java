package com.mimaja.job_finder_app.feature.contract.service;

import com.mimaja.job_finder_app.feature.contract.dto.request.ContractDevelopRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUpdateRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUploadRequestDto;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;

public interface ContractService {
    void uploadContract(ContractUploadRequestDto requestDto, JwtPrincipal principal);

    void updateContract(ContractUpdateRequestDto requestDto, JwtPrincipal principal);

    void acceptContract(ContractDevelopRequestDto requestDto, JwtPrincipal principal);

    void declineContract(ContractDevelopRequestDto requestDto, JwtPrincipal principal);
}
