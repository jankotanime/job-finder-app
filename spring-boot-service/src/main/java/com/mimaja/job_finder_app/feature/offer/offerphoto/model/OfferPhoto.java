package com.mimaja.job_finder_app.feature.offer.offerphoto.model;

import com.mimaja.job_finder_app.feature.offer.offerphoto.dto.OfferPhotoCreateRequestDto;
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
public class OfferPhoto {
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

    public static OfferPhoto from(OfferPhotoCreateRequestDto dto) {
        OfferPhoto photo = new OfferPhoto();
        photo.setFileName(dto.fileName());
        photo.setMimeType(dto.mimeType());
        photo.setFileSize(dto.fileSize());
        photo.setStorageKey(dto.storageKey());
        return photo;
    }
}
