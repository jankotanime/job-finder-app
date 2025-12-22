package com.mimaja.job_finder_app.feature.cv.model;

import com.mimaja.job_finder_app.feature.cv.dto.CvUpdateRequestDto;
import com.mimaja.job_finder_app.feature.cv.dto.CvUploadRequestDto;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "cvs")
public class Cv {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fileName;

    @Enumerated(EnumType.STRING)
    private MimeType mimeType;

    private BigInteger fileSize;

    private String storageKey;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreationTimestamp private LocalDateTime createdAt;

    @UpdateTimestamp private LocalDateTime updatedAt;

    public static Cv from(CvUploadRequestDto dto) {
        Cv cv = new Cv();
        cv.fileName = dto.fileName();
        cv.mimeType = dto.mimeType();
        cv.fileSize = dto.fileSize();
        cv.storageKey = dto.storageKey();
        cv.user = dto.user();
        return cv;
    }

    public void update(CvUpdateRequestDto dto) {
        this.fileName = dto.fileName();
        this.mimeType = dto.mimeType();
        this.fileSize = dto.fileSize();
        this.storageKey = dto.storageKey();
    }
}
