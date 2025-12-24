package com.mimaja.job_finder_app.feature.user.profilephoto.model;

import com.mimaja.job_finder_app.feature.user.profilephoto.dto.ProfilePhotoCreateRequestDto;
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
public class ProfilePhoto extends FileBase {
    public static ProfilePhoto from(FileBase fileBase) {
        if (fileBase == null) return null;
        return builder()
                .fileName(fileBase.getFileName())
                .mimeType(fileBase.getMimeType())
                .fileSize(fileBase.getFileSize())
                .storageKey(fileBase.getStorageKey())
                .build();
    }

    public static ProfilePhoto from(ProfilePhotoCreateRequestDto dto) {
        return builder()
                .fileName(dto.fileName())
                .mimeType(dto.mimeType())
                .fileSize(dto.fileSize())
                .storageKey(dto.storageKey())
                .build();
    }
}
