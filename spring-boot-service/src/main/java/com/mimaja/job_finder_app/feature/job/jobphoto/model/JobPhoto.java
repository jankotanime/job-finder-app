package com.mimaja.job_finder_app.feature.job.jobphoto.model;

import com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto;
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
public class JobPhoto extends FileBase {
    public static JobPhoto from(FileBase file) {
        if (file == null) return null;
        return builder()
                .fileName(file.getFileName())
                .mimeType(file.getMimeType())
                .fileSize(file.getFileSize())
                .storageKey(file.getStorageKey())
                .build();
    }

    public static JobPhoto from(OfferPhoto photo, String storageKey) {
        return builder()
                .fileName(photo.getFileName())
                .mimeType(photo.getMimeType())
                .fileSize(photo.getFileSize())
                .storageKey(storageKey)
                .build();
    }
}
