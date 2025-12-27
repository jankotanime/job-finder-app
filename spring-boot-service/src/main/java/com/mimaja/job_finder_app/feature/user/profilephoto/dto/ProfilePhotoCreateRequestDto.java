package com.mimaja.job_finder_app.feature.user.profilephoto.dto;

import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfilePhotoCreateRequestDto(
        @NotBlank String fileName,
        @NotNull MimeType mimeType,
        @NotNull long fileSize,
        @NotBlank String storageKey) {
    public static ProfilePhotoCreateRequestDto from(ProcessedFileDetails details) {
        return new ProfilePhotoCreateRequestDto(
                details.fileName(), details.mimeType(), details.fileSize(), details.storageKey());
    }
}
