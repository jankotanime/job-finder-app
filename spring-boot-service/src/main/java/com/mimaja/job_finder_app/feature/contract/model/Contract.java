package com.mimaja.job_finder_app.feature.contract.model;

import com.mimaja.job_finder_app.feature.contract.dto.cloudflare.ContractCFRequestDto;
import com.mimaja.job_finder_app.feature.contract.enums.ContractStatus;
import com.mimaja.job_finder_app.feature.job.model.Job;
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
    private ContractStatus contractorAcceptance;

    @OneToOne(fetch = FetchType.LAZY)
    private Offer offer;

    @OneToOne(fetch = FetchType.LAZY)
    private Job job;

    public static Contract from(FileBase fileBase) {
        if (fileBase == null) return null;
        return builder()
                .fileName(fileBase.getFileName())
                .mimeType(fileBase.getMimeType())
                .fileSize(fileBase.getFileSize())
                .storageKey(fileBase.getStorageKey())
                .contractorAcceptance(ContractStatus.WAITING)
                .build();
    }

    public static Contract from(ContractCFRequestDto dto, Offer offer) {
        return builder()
                .fileName(dto.fileName())
                .mimeType(dto.mimeType())
                .fileSize(dto.fileSize())
                .storageKey(dto.storageKey())
                .contractorAcceptance(ContractStatus.WAITING)
                .offer(offer)
                .build();
    }
}
