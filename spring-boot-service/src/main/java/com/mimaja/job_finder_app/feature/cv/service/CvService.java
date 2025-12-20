package com.mimaja.job_finder_app.feature.cv.service;

import com.mimaja.job_finder_app.feature.cv.model.Cv;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface CvService {
    Cv uploadCv(MultipartFile file, UUID userId);

    Cv getCvById(UUID cvId);

    List<Cv> getCvsByUserId(UUID userId);

    Cv updateCv(MultipartFile file, UUID cvId);

    void deleteCv(UUID cvId);

    void deleteAllCvsForUser(UUID userId);
}
