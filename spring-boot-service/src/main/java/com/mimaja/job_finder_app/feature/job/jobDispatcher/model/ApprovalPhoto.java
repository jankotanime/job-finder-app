package com.mimaja.job_finder_app.feature.job.jobDispatcher.model;

import com.mimaja.job_finder_app.feature.job.jobDispatcher.dto.JobDispatcherPhotoCreateRequestDto;
import com.mimaja.job_finder_app.shared.model.FileBase;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ApprovalPhoto extends FileBase {
    public static ApprovalPhoto from(FileBase file) {
        if (file == null) return null;
        return builder()
                .fileName(file.getFileName())
                .mimeType(file.getMimeType())
                .fileSize(file.getFileSize())
                .storageKey(file.getStorageKey())
                .build();
    }

    public static ApprovalPhoto from(JobDispatcherPhotoCreateRequestDto dto) {
        return builder()
                .fileName(dto.fileName())
                .mimeType(dto.mimeType())
                .fileSize(dto.fileSize())
                .storageKey(dto.storageKey())
                .build();
    }
}
