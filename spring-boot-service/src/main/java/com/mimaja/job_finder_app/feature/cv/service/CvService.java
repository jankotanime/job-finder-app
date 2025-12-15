package com.mimaja.job_finder_app.feature.cv.service;

import com.mimaja.job_finder_app.feature.cv.dto.CvUploadRequestDto;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface CvService {
    String uploadFile(MultipartFile file);

    Cv uploadCv(CvUploadRequestDto dto, UUID userId);

    Cv getCvById(UUID cvId);

    List<Cv> getCvsByUserId(UUID userId);

    Cv updateCv(CvUploadRequestDto dto, UUID cvId, UUID userId);

    void deleteCv(UUID cvId);

    void deleteAllCvsForUser(UUID userId);
}
