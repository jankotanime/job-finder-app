package com.mimaja.job_finder_app.feature.contract.service;

import com.mimaja.job_finder_app.feature.contract.dto.request.ContractDevelopRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUpdateRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUploadRequestDto;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;

public class ContractServiceDefault implements ContractService {
    @Override
    public void uploadContract(ContractUploadRequestDto requestDto, JwtPrincipal principal) {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateContract(ContractUpdateRequestDto requestDto, JwtPrincipal principal) {}

    @Override
    public void acceptContract(ContractDevelopRequestDto requestDto, JwtPrincipal principal) {
        // TODO Auto-generated method stub
    }

    @Override
    public void declineContract(ContractDevelopRequestDto requestDto, JwtPrincipal principal) {
        // TODO Auto-generated method stub
    }
}
