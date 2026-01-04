package com.mimaja.job_finder_app.feature.contract.dto.response;

import com.mimaja.job_finder_app.feature.contract.model.Contract;
import java.util.UUID;

public record ContractDto(UUID id, String storageKey, UUID offerId) {
    public static ContractDto from(Contract contract) {
        return new ContractDto(
                contract.getId(), contract.getStorageKey(), contract.getOffer().getId());
    }
}
