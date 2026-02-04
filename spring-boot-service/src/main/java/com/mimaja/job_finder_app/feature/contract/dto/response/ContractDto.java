package com.mimaja.job_finder_app.feature.contract.dto.response;

import com.mimaja.job_finder_app.feature.contract.enums.ContractStatus;
import com.mimaja.job_finder_app.feature.contract.model.Contract;
import java.util.UUID;

public record ContractDto(
        UUID id, String storageKey, ContractStatus status, UUID jobId, UUID offerId) {
    public static ContractDto from(Contract contract) {
        return new ContractDto(
                contract.getId(),
                contract.getStorageKey(),
                contract.getContractorAcceptance(),
                contract.getJob() != null ? contract.getJob().getId() : null,
                contract.getOffer() != null ? contract.getOffer().getId() : null);
    }
}
