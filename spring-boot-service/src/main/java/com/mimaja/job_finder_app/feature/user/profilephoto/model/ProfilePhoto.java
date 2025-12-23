package com.mimaja.job_finder_app.feature.user.profilephoto.model;

import com.mimaja.job_finder_app.feature.user.profilephoto.dto.ProfilePhotoCreateRequestDto;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProfilePhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fileName;

    @Enumerated(EnumType.STRING)
    private MimeType mimeType;

    private BigInteger fileSize;

    private String storageKey;

    @CreationTimestamp private LocalDateTime createdAt;

    @UpdateTimestamp private LocalDateTime updatedAt;

    public static ProfilePhoto from(ProfilePhotoCreateRequestDto dto) {
        ProfilePhoto photo = new ProfilePhoto();
        photo.setFileName(dto.fileName());
        photo.setMimeType(dto.mimeType());
        photo.setFileSize(dto.fileSize());
        photo.setStorageKey(dto.storageKey());
        return photo;
    }
}
