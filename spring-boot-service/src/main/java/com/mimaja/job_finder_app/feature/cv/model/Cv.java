package com.mimaja.job_finder_app.feature.cv.model;

import com.mimaja.job_finder_app.feature.cv.dto.CvUpdateRequestDto;
import com.mimaja.job_finder_app.feature.cv.dto.CvUploadRequestDto;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.model.FileBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
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
@Table(name = "cvs")
public class Cv extends FileBase {
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static Cv from(FileBase fileBase) {
        if (fileBase == null) return null;
        return builder()
                .fileName(fileBase.getFileName())
                .mimeType(fileBase.getMimeType())
                .fileSize(fileBase.getFileSize())
                .storageKey(fileBase.getStorageKey())
                .build();
    }

    public static Cv from(CvUploadRequestDto dto) {
        return builder()
                .fileName(dto.fileName())
                .mimeType(dto.mimeType())
                .fileSize(dto.fileSize())
                .storageKey(dto.storageKey())
                .user(dto.user())
                .build();
    }

    public void update(CvUpdateRequestDto dto) {
        this.fileName = dto.fileName();
        this.mimeType = dto.mimeType();
        this.fileSize = dto.fileSize();
        this.storageKey = dto.storageKey();
    }
}
