package com.mimaja.job_finder_app.shared.model;

import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FileBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;

    protected String fileName;

    @Enumerated(EnumType.STRING)
    protected MimeType mimeType;

    protected long fileSize;

    protected String storageKey;

    @CreationTimestamp protected LocalDateTime createdAt;

    @UpdateTimestamp protected LocalDateTime updatedAt;
}
