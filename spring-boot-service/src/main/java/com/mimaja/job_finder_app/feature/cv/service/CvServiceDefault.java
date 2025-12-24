package com.mimaja.job_finder_app.feature.cv.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.cv.dto.CvUpdateRequestDto;
import com.mimaja.job_finder_app.feature.cv.dto.CvUploadRequestDto;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.cv.repository.CvRepository;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import com.mimaja.job_finder_app.shared.adapters.MultipartFileSource;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.FileFolderName;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CvServiceDefault implements CvService {
    private final CvRepository cvRepository;
    private final UserService userService;
    private final FileManagementService fileManagementService;

    @Override
    @Transactional
    public Cv uploadCv(MultipartFile file, UUID userId) {
        User user = userService.getUserById(userId);
        MultipartFileSource fileSource = new MultipartFileSource(file);
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);
        ProcessedFileDetails fileDetails =
                fileManagementService.processFileDetails(fileSource, folder);

        fileManagementService.uploadFile(fileDetails);

        CvUploadRequestDto dto = CvUploadRequestDto.from(fileDetails, user);

        Cv cv = Cv.from(dto);
        return cvRepository.save(cv);
    }

    @Override
    public Cv getCvById(UUID cvId) {
        return getOrThrow(cvId);
    }

    @Override
    public List<Cv> getCvsByUserId(UUID userId) {
        return cvRepository.findAllCvsByUserId(userId);
    }

    @Override
    @Transactional
    public Cv updateCv(MultipartFile file, UUID cvId) {
        Cv cv = getOrThrow(cvId);
        MultipartFileSource fileSource = new MultipartFileSource(file);
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);
        ProcessedFileDetails fileDetails =
                fileManagementService.processFileDetails(fileSource, folder);

        fileManagementService.deleteFile(cv.getStorageKey());
        fileManagementService.uploadFile(fileDetails);

        CvUpdateRequestDto dto = CvUpdateRequestDto.from(fileDetails);

        cv.update(dto);
        return cvRepository.save(cv);
    }

    @Override
    @Transactional
    public void deleteCv(UUID cvId) {
        Cv cv = getOrThrow(cvId);
        fileManagementService.deleteFile(cv.getStorageKey());
    }

    @Override
    @Transactional
    public void deleteAllCvsForUser(UUID userId) {
        List<Cv> cvList = cvRepository.findAllCvsByUserId(userId);
        cvList.forEach(cv -> fileManagementService.deleteFile(cv.getStorageKey()));
        cvRepository.deleteAll(cvList);
    }

    private Cv getOrThrow(UUID cvId) {
        return cvRepository
                .findById(cvId)
                .orElseThrow(() -> new BusinessException(BusinessExceptionReason.CV_NOT_FOUND));
    }
}
