package com.mimaja.job_finder_app.feature.unit.cv.service;

import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.TEST_CV_STORAGE_KEY;
import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.createTestCv;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.cv.repository.CvRepository;
import com.mimaja.job_finder_app.feature.cv.service.CvServiceDefault;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CvServiceDefaultTest {
    @Mock private CvRepository cvRepository;
    @Mock private UserService userService;
    @Mock private FileManagementService fileManagementService;

    private CvServiceDefault cvService;
    private Cv testCv;
    private User testUser;

    @BeforeEach
    void setUp() {
        testCv = createTestCv();
        testUser = createTestUser();
        cvService = new CvServiceDefault(cvRepository, userService, fileManagementService);
    }

    // --- uploadCv ---

    @Test
    void uploadCv_shouldReturnNonNullCv_whenFileUploadSucceeds() {
        setupUploadMocks();
        Cv result = cvService.uploadCv(createMockFile(), testUser.getId());
        assertThat(result).isNotNull();
    }

    @Test
    void uploadCv_shouldCallRepositorySave_whenFileUploadSucceeds() {
        setupUploadMocks();
        cvService.uploadCv(createMockFile(), testUser.getId());
        verify(cvRepository, times(1)).save(any(Cv.class));
    }

    @Test
    void uploadCv_shouldCallFileUpload_whenFileUploadSucceeds() {
        setupUploadMocks();
        cvService.uploadCv(createMockFile(), testUser.getId());
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    @Test
    void uploadCv_shouldCallGetUserById_whenInvoked() {
        UUID userId = testUser.getId();
        setupUploadMocks();
        cvService.uploadCv(createMockFile(), userId);
        verify(userService, times(1)).getUserById(userId);
    }

    // --- getCvById ---

    @Test
    void getCvById_shouldReturnCv_whenCvExists() {
        UUID cvId = testCv.getId();
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(testCv));
        Cv result = cvService.getCvById(cvId);
        assertThat(result).isNotNull();
    }

    @Test
    void getCvById_shouldReturnCorrectCv_whenCvExists() {
        UUID cvId = testCv.getId();
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(testCv));
        Cv result = cvService.getCvById(cvId);
        assertThat(result.getId()).isEqualTo(cvId);
    }

    @Test
    void getCvById_shouldThrowExceptionWithCvNotFoundCode_whenCvNotFound() {
        UUID cvId = UUID.randomUUID();
        when(cvRepository.findById(cvId)).thenReturn(Optional.empty());
        BusinessException exception =
                assertThrows(BusinessException.class, () -> cvService.getCvById(cvId));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.CV_NOT_FOUND.getCode());
    }

    @Test
    void getCvById_shouldCallFindById_whenCvNotFound() {
        UUID cvId = UUID.randomUUID();
        when(cvRepository.findById(cvId)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> cvService.getCvById(cvId));
        verify(cvRepository, times(1)).findById(cvId);
    }

    // --- getCvsByUserId ---

    @Test
    void getCvsByUserId_shouldReturnCvList_whenUserHasCvs() {
        UUID userId = testUser.getId();
        when(cvRepository.findAllCvsByUserId(userId)).thenReturn(List.of(testCv));
        List<Cv> result = cvService.getCvsByUserId(userId);
        assertThat(result).hasSize(1);
    }

    @Test
    void getCvsByUserId_shouldReturnEmptyList_whenUserHasNoCvs() {
        UUID userId = testUser.getId();
        when(cvRepository.findAllCvsByUserId(userId)).thenReturn(List.of());
        List<Cv> result = cvService.getCvsByUserId(userId);
        assertThat(result).isEmpty();
    }

    @Test
    void getCvsByUserId_shouldCallRepository_whenInvoked() {
        UUID userId = testUser.getId();
        when(cvRepository.findAllCvsByUserId(userId)).thenReturn(List.of());
        cvService.getCvsByUserId(userId);
        verify(cvRepository, times(1)).findAllCvsByUserId(userId);
    }

    // --- updateCv ---

    @Test
    void updateCv_shouldReturnNonNullCv_whenCvExists() {
        UUID cvId = testCv.getId();
        setupUpdateMocks(cvId);
        Cv result = cvService.updateCv(createMockFile(), cvId);
        assertThat(result).isNotNull();
    }

    @Test
    void updateCv_shouldThrowExceptionWithCvNotFoundCode_whenCvNotFound() {
        UUID cvId = UUID.randomUUID();
        when(cvRepository.findById(cvId)).thenReturn(Optional.empty());
        BusinessException exception =
                assertThrows(
                        BusinessException.class, () -> cvService.updateCv(createMockFile(), cvId));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.CV_NOT_FOUND.getCode());
    }

    @Test
    void updateCv_shouldCallDeleteFile_whenCvExists() {
        UUID cvId = testCv.getId();
        setupUpdateMocks(cvId);
        cvService.updateCv(createMockFile(), cvId);
        verify(fileManagementService, times(1)).deleteFile(TEST_CV_STORAGE_KEY);
    }

    @Test
    void updateCv_shouldCallUploadFile_whenCvExists() {
        UUID cvId = testCv.getId();
        setupUpdateMocks(cvId);
        cvService.updateCv(createMockFile(), cvId);
        verify(fileManagementService, times(1)).uploadFile(any());
    }

    @Test
    void updateCv_shouldCallRepositorySave_whenCvExists() {
        UUID cvId = testCv.getId();
        setupUpdateMocks(cvId);
        cvService.updateCv(createMockFile(), cvId);
        verify(cvRepository, times(1)).save(any(Cv.class));
    }

    // --- deleteCv ---

    @Test
    void deleteCv_shouldCallRepositoryDelete_whenCvExists() {
        UUID cvId = testCv.getId();
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(testCv));
        cvService.deleteCv(cvId);
        verify(cvRepository, times(1)).delete(testCv);
    }

    @Test
    void deleteCv_shouldCallDeleteFile_whenCvExists() {
        UUID cvId = testCv.getId();
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(testCv));
        cvService.deleteCv(cvId);
        verify(fileManagementService, times(1)).deleteFile(TEST_CV_STORAGE_KEY);
    }

    @Test
    void deleteCv_shouldThrowExceptionWithCvNotFoundCode_whenCvNotFound() {
        UUID cvId = UUID.randomUUID();
        when(cvRepository.findById(cvId)).thenReturn(Optional.empty());
        BusinessException exception =
                assertThrows(BusinessException.class, () -> cvService.deleteCv(cvId));
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.CV_NOT_FOUND.getCode());
    }

    // --- deleteAllCvsForUser ---

    @Test
    void deleteAllCvsForUser_shouldCallRepositoryDeleteAll_whenUserHasCvs() {
        UUID userId = testUser.getId();
        when(cvRepository.findAllCvsByUserId(userId)).thenReturn(List.of(testCv));
        cvService.deleteAllCvsForUser(userId);
        verify(cvRepository, times(1)).deleteAll(List.of(testCv));
    }

    @Test
    void deleteAllCvsForUser_shouldCallDeleteFileForEachCv_whenUserHasCvs() {
        UUID userId = testUser.getId();
        when(cvRepository.findAllCvsByUserId(userId)).thenReturn(List.of(testCv));
        cvService.deleteAllCvsForUser(userId);
        verify(fileManagementService, times(1)).deleteFile(TEST_CV_STORAGE_KEY);
    }

    @Test
    void deleteAllCvsForUser_shouldNotCallDeleteFile_whenUserHasNoCvs() {
        UUID userId = testUser.getId();
        when(cvRepository.findAllCvsByUserId(userId)).thenReturn(List.of());
        cvService.deleteAllCvsForUser(userId);
        verify(fileManagementService, times(0)).deleteFile(any());
    }

    @Test
    void deleteAllCvsForUser_shouldCallRepositoryDeleteAll_whenUserHasNoCvs() {
        UUID userId = testUser.getId();
        when(cvRepository.findAllCvsByUserId(userId)).thenReturn(List.of());
        cvService.deleteAllCvsForUser(userId);
        verify(cvRepository, times(1)).deleteAll(List.of());
    }

    private void setupUploadMocks() {
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(fileManagementService.processFileDetails(any(), any()))
                .thenReturn(createTestFileDetails());
        when(cvRepository.save(any(Cv.class))).thenReturn(testCv);
    }

    private void setupUpdateMocks(UUID cvId) {
        when(cvRepository.findById(cvId)).thenReturn(Optional.of(testCv));
        when(fileManagementService.processFileDetails(any(), any()))
                .thenReturn(createTestFileDetails());
        when(cvRepository.save(any(Cv.class))).thenReturn(testCv);
    }

    private MultipartFile createMockFile() {
        return mock(MultipartFile.class);
    }

    private ProcessedFileDetails createTestFileDetails() {
        return new ProcessedFileDetails(
                "test_cv.pdf",
                "application/pdf",
                MimeType.PDF,
                TEST_CV_STORAGE_KEY,
                2048L,
                new byte[] {1, 2, 3, 4});
    }
}
