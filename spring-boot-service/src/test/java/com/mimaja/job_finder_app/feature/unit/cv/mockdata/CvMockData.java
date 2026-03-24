package com.mimaja.job_finder_app.feature.unit.cv.mockdata;

import com.mimaja.job_finder_app.feature.cv.dto.CvUploadRequestDto;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import java.util.UUID;

public class CvMockData {
    public static final String TEST_CV_FILENAME = "test_cv.pdf";
    public static final String TEST_CV_STORAGE_KEY = "test-storage-key-123";
    public static final long TEST_CV_FILE_SIZE = 2048L;

    public static CvUploadRequestDto createTestCvUploadRequestDto(User user) {
        return new CvUploadRequestDto(
                TEST_CV_FILENAME,
                MimeType.PDF,
                TEST_CV_FILE_SIZE,
                TEST_CV_STORAGE_KEY,
                user
        );
    }

    public static CvUploadRequestDto createTestCvUploadRequestDtoWithNullFileName(User user) {
        return new CvUploadRequestDto(
                null,
                MimeType.PDF,
                TEST_CV_FILE_SIZE,
                TEST_CV_STORAGE_KEY,
                user
        );
    }

    public static CvUploadRequestDto createTestCvUploadRequestDtoWithNullMimeType(User user) {
        return new CvUploadRequestDto(
                TEST_CV_FILENAME,
                null,
                TEST_CV_FILE_SIZE,
                TEST_CV_STORAGE_KEY,
                user
        );
    }

    public static CvUploadRequestDto createTestCvUploadRequestDtoWithNullStorageKey(User user) {
        return new CvUploadRequestDto(
                TEST_CV_FILENAME,
                MimeType.PDF,
                TEST_CV_FILE_SIZE,
                null,
                user
        );
    }

    public static Cv createTestCv() {
        return Cv.builder()
                .id(UUID.randomUUID())
                .fileName(TEST_CV_FILENAME)
                .mimeType(MimeType.PDF)
                .fileSize(TEST_CV_FILE_SIZE)
                .storageKey(TEST_CV_STORAGE_KEY)
                .build();
    }

    public static Cv createTestCvWithNullStorageKey() {
        return Cv.builder()
                .id(UUID.randomUUID())
                .fileName(TEST_CV_FILENAME)
                .mimeType(MimeType.PDF)
                .fileSize(TEST_CV_FILE_SIZE)
                .storageKey(null)
                .build();
    }
}
