package com.mimaja.job_finder_app.feature.cv.dto;

import com.mimaja.job_finder_app.feature.cv.model.MimeType;
import java.math.BigInteger;

public record CvUploadRequestDto(String fileName, MimeType mimeType, BigInteger fileSize) {}
