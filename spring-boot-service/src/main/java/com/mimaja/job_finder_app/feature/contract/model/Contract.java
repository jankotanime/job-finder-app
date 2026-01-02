package com.mimaja.job_finder_app.feature.contract.model;

import com.mimaja.job_finder_app.feature.contract.dto.cloudflare.ContractUploadCFRequestDto;
import com.mimaja.job_finder_app.feature.contract.enums.ContractStatus;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.shared.model.FileBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Table(name = "contracts")
public class Contract extends FileBase {
    @OneToOne(fetch = FetchType.LAZY)
    private Offer offer;

    private ContractStatus contractorAcceptance;

    public static Contract from(FileBase fileBase) {
        if (fileBase == null) return null;
        return builder()
                .fileName(fileBase.getFileName())
                .mimeType(fileBase.getMimeType())
                .fileSize(fileBase.getFileSize())
                .storageKey(fileBase.getStorageKey())
                .build();
    }

    public static Contract from(ContractUploadCFRequestDto dto) {
        return builder()
                .fileName(dto.fileName())
                .mimeType(dto.mimeType())
                .fileSize(dto.fileSize())
                .storageKey(dto.storageKey())
                .offer(dto.offer())
                .build();
    }
}
