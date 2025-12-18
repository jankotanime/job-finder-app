package com.mimaja.job_finder_app.feature.cv.dto;

import com.mimaja.job_finder_app.feature.cv.model.MimeType;
import java.math.BigInteger;
import java.util.UUID;

public record CvResponseDto(
        UUID id, String fileName, MimeType mimeType, BigInteger fileSize, String storageKey) {}
