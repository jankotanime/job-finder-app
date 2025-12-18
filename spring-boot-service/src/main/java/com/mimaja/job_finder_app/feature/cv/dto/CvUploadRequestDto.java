package com.mimaja.job_finder_app.feature.cv.dto;

import com.mimaja.job_finder_app.feature.cv.model.MimeType;
import com.mimaja.job_finder_app.feature.user.model.User;
import java.math.BigInteger;

public record CvUploadRequestDto(
        String fileName, MimeType mimeType, BigInteger fileSize, String storageKey, User user) {}
