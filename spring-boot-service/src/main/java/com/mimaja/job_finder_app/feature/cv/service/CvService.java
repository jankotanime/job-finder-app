package com.mimaja.job_finder_app.feature.cv.service;

import com.mimaja.job_finder_app.feature.cv.dto.CvResponseDto;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface CvService {
    CvResponseDto uploadCv(MultipartFile file, UUID userId);

    CvResponseDto getCvById(UUID cvId);

    List<CvResponseDto> getCvsByUserId(UUID userId);

    CvResponseDto updateCv(MultipartFile file, UUID cvId);

    void deleteCv(UUID cvId);

    void deleteAllCvsForUser(UUID userId);
}
