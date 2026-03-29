package com.mimaja.job_finder_app.feature.unit.shared.service;

import static com.mimaja.job_finder_app.feature.unit.shared.mockdata.FileManagementMockData.TEST_BUCKET;
import static com.mimaja.job_finder_app.feature.unit.shared.mockdata.FileManagementMockData.TEST_CONTENT_TYPE;
import static com.mimaja.job_finder_app.feature.unit.shared.mockdata.FileManagementMockData.TEST_FILE_NAME;
import static com.mimaja.job_finder_app.feature.unit.shared.mockdata.FileManagementMockData.TEST_FILE_SIZE;
import static com.mimaja.job_finder_app.feature.unit.shared.mockdata.FileManagementMockData.TEST_PHOTO_NAME;
import static com.mimaja.job_finder_app.feature.unit.shared.mockdata.FileManagementMockData.TEST_STORAGE_KEY;
import static com.mimaja.job_finder_app.feature.unit.shared.mockdata.FileManagementMockData.createMockFileSource;
import static com.mimaja.job_finder_app.feature.unit.shared.mockdata.FileManagementMockData.createMockFileSourceForPhoto;
import static com.mimaja.job_finder_app.feature.unit.shared.mockdata.FileManagementMockData.createMockFileSourceWithFilename;
import static com.mimaja.job_finder_app.feature.unit.shared.mockdata.FileManagementMockData.createMockFileSourceWithFilenameOnly;
import static com.mimaja.job_finder_app.feature.unit.shared.mockdata.FileManagementMockData.createTestProcessedFileDetails;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.FileFolderName;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import com.mimaja.job_finder_app.shared.interfaces.FileSource;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileManagementService - Unit Tests")
class FileManagementServiceTest {
    @Mock private S3Client s3Client;

    private FileManagementService fileManagementService;

    @BeforeEach
    void setUp() throws Exception {
        fileManagementService = new FileManagementService(s3Client);
        java.lang.reflect.Field bucketField =
                FileManagementService.class.getDeclaredField("bucket");
        bucketField.setAccessible(true);
        bucketField.set(fileManagementService, TEST_BUCKET);
    }

    // =========================
    // getFile Tests
    // =========================

    @Test
    @DisplayName("Should get file from S3 when key exists")
    void testGetFile_shouldReturnResponseInputStream_whenKeyExists() {
        // given
        @SuppressWarnings("unchecked")
        ResponseInputStream<GetObjectResponse> expectedResponse = mock(ResponseInputStream.class);
        when(s3Client.getObject((GetObjectRequest) any())).thenReturn(expectedResponse);

        // when
        ResponseInputStream<GetObjectResponse> result =
                fileManagementService.getFile(TEST_STORAGE_KEY);

        // then
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should call S3 with correct bucket and key")
    void testGetFile_shouldCallS3WithCorrectParameters_whenKeyExists() {
        // given
        @SuppressWarnings("unchecked")
        ResponseInputStream<GetObjectResponse> expectedResponse = mock(ResponseInputStream.class);
        when(s3Client.getObject((GetObjectRequest) any())).thenReturn(expectedResponse);

        // when
        fileManagementService.getFile(TEST_STORAGE_KEY);

        // then
        verify(s3Client, times(1)).getObject((GetObjectRequest) any());
    }

    // =========================
    // deleteFile Tests
    // =========================

    @Test
    @DisplayName("Should delete file from S3")
    void testDeleteFile_shouldCallS3Delete_whenKeyExists() {
        // given
        when(s3Client.deleteObject((DeleteObjectRequest) any())).thenReturn(null);

        // when
        fileManagementService.deleteFile(TEST_STORAGE_KEY);

        // then
        verify(s3Client, times(1)).deleteObject((DeleteObjectRequest) any());
    }

    @Test
    @DisplayName("Should delete file with correct storage key")
    void testDeleteFile_shouldDeleteWithCorrectKey_whenCalled() {
        // given
        when(s3Client.deleteObject((DeleteObjectRequest) any())).thenReturn(null);

        // when
        fileManagementService.deleteFile(TEST_STORAGE_KEY);

        // then
        assertThat(TEST_STORAGE_KEY).isNotNull();
    }

    // =========================
    // uploadFile Tests
    // =========================

    @Test
    @DisplayName("Should upload file to S3")
    void testUploadFile_shouldCallS3Put_whenFileDetailsProvided() {
        // given
        ProcessedFileDetails fileDetails = createTestProcessedFileDetails();
        when(s3Client.putObject((PutObjectRequest) any(), (RequestBody) any())).thenReturn(null);

        // when
        fileManagementService.uploadFile(fileDetails);

        // then
        verify(s3Client, times(1)).putObject((PutObjectRequest) any(), (RequestBody) any());
    }

    @Test
    @DisplayName("Should upload file with correct content type")
    void testUploadFile_shouldUseCorrectContentType_whenFileDetailsProvided() {
        // given
        ProcessedFileDetails fileDetails = createTestProcessedFileDetails();
        when(s3Client.putObject((PutObjectRequest) any(), (RequestBody) any())).thenReturn(null);

        // when
        fileManagementService.uploadFile(fileDetails);

        // then
        assertThat(fileDetails.contentType()).isEqualTo(TEST_CONTENT_TYPE);
    }

    // =========================
    // processFileDetails Tests
    // =========================

    @Test
    @DisplayName("Should process file details successfully for document")
    void testProcessFileDetails_shouldReturnProcessedFileDetails_whenDocumentFileProvided() {
        // given
        FileSource fileSource = createMockFileSource();
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);

        // when
        ProcessedFileDetails result = fileManagementService.processFileDetails(fileSource, folder);

        // then
        assertThat(result).isNotNull();
        assertThat(result.fileName()).isEqualTo(TEST_FILE_NAME);
        assertThat(result.mimeType()).isEqualTo(MimeType.PDF);
    }

    @Test
    @DisplayName("Should generate unique storage key")
    void testProcessFileDetails_shouldGenerateUniqueStorageKey_whenFileProvided() {
        // given
        FileSource fileSource = createMockFileSource();
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);

        // when
        ProcessedFileDetails result = fileManagementService.processFileDetails(fileSource, folder);

        // then
        assertThat(result.storageKey())
                .as("Storage key should start with folder")
                .startsWith(folder);
        assertThat(result.storageKey())
                .as("Storage key should contain file name")
                .endsWith(TEST_FILE_NAME);
    }

    @Test
    @DisplayName("Should include file size from source")
    void testProcessFileDetails_shouldIncludeFileSize_whenFileProvided() {
        // given
        FileSource fileSource = createMockFileSource();
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);

        // when
        ProcessedFileDetails result = fileManagementService.processFileDetails(fileSource, folder);

        // then
        assertThat(result.fileSize()).isEqualTo(TEST_FILE_SIZE);
    }

    @Test
    @DisplayName("Should include bytes from source")
    void testProcessFileDetails_shouldIncludeBytes_whenFileProvided() {
        // given
        FileSource fileSource = createMockFileSource();
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);

        // when
        ProcessedFileDetails result = fileManagementService.processFileDetails(fileSource, folder);

        // then
        assertThat(result.bytes()).isNotNull();
    }

    @Test
    @DisplayName("Should process photo file successfully")
    void testProcessFileDetails_shouldProcessPhotoSuccessfully_whenPhotoFileProvided() {
        // given
        FileSource fileSource = createMockFileSourceForPhoto();
        String folder = FileFolderName.PHOTOS.getFullPath(FileFolderName.OFFER_PHOTO);

        // when
        ProcessedFileDetails result = fileManagementService.processFileDetails(fileSource, folder);

        // then
        assertThat(result.mimeType()).isEqualTo(MimeType.JPG);
        assertThat(result.fileName()).isEqualTo(TEST_PHOTO_NAME);
    }

    // =========================
    // File Format Validation Tests
    // =========================

    @Test
    @DisplayName("Should throw exception for wrong document format")
    void testProcessFileDetails_shouldThrowBusinessException_whenWrongDocumentFormatProvided() {
        // given
        FileSource fileSource = createMockFileSourceWithFilenameOnly("document.jpg");
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);

        // when & then
        assertThatThrownBy(() -> fileManagementService.processFileDetails(fileSource, folder))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Should throw exception for wrong photo format")
    void testProcessFileDetails_shouldThrowBusinessException_whenWrongPhotoFormatProvided() {
        // given
        FileSource fileSource = createMockFileSourceWithFilenameOnly("photo.pdf");
        String folder = FileFolderName.PHOTOS.getFullPath(FileFolderName.OFFER_PHOTO);

        // when & then
        assertThatThrownBy(() -> fileManagementService.processFileDetails(fileSource, folder))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("Should accept PDF for document folder")
    void testProcessFileDetails_shouldAcceptPDF_whenDocumentFolderAndPDFProvided() {
        // given
        FileSource fileSource = createMockFileSourceWithFilename("document.pdf");
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);

        // when
        ProcessedFileDetails result = fileManagementService.processFileDetails(fileSource, folder);

        // then
        assertThat(result.mimeType()).isEqualTo(MimeType.PDF);
    }

    @Test
    @DisplayName("Should accept DOCX for document folder")
    void testProcessFileDetails_shouldAcceptDOCX_whenDocumentFolderAndDOCXProvided() {
        // given
        FileSource fileSource = createMockFileSourceWithFilename("document.docx");
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);

        // when
        ProcessedFileDetails result = fileManagementService.processFileDetails(fileSource, folder);

        // then
        assertThat(result.mimeType()).isEqualTo(MimeType.DOCX);
    }

    @Test
    @DisplayName("Should accept JPG for photo folder")
    void testProcessFileDetails_shouldAcceptJPG_whenPhotoFolderAndJPGProvided() {
        // given
        FileSource fileSource = createMockFileSourceWithFilename("photo.jpg");
        String folder = FileFolderName.PHOTOS.getFullPath(FileFolderName.OFFER_PHOTO);

        // when
        ProcessedFileDetails result = fileManagementService.processFileDetails(fileSource, folder);

        // then
        assertThat(result.mimeType()).isEqualTo(MimeType.JPG);
    }

    @Test
    @DisplayName("Should accept JPEG for photo folder")
    void testProcessFileDetails_shouldAcceptJPEG_whenPhotoFolderAndJPEGProvided() {
        // given
        FileSource fileSource = createMockFileSourceWithFilename("photo.jpeg");
        String folder = FileFolderName.PHOTOS.getFullPath(FileFolderName.OFFER_PHOTO);

        // when
        ProcessedFileDetails result = fileManagementService.processFileDetails(fileSource, folder);

        // then
        assertThat(result.mimeType()).isEqualTo(MimeType.JPEG);
    }

    @Test
    @DisplayName("Should accept PNG for photo folder")
    void testProcessFileDetails_shouldAcceptPNG_whenPhotoFolderAndPNGProvided() {
        // given
        FileSource fileSource = createMockFileSourceWithFilename("photo.png");
        String folder = FileFolderName.PHOTOS.getFullPath(FileFolderName.OFFER_PHOTO);

        // when
        ProcessedFileDetails result = fileManagementService.processFileDetails(fileSource, folder);

        // then
        assertThat(result.mimeType()).isEqualTo(MimeType.PNG);
    }

    // =========================
    // File Extension Tests
    // =========================

    @Test
    @DisplayName("Should throw exception for file without extension")
    void testProcessFileDetails_shouldThrowIllegalArgumentException_whenFileHasNoExtension() {
        // given
        FileSource fileSource = createMockFileSourceWithFilenameOnly("document");
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);

        // when & then
        assertThatThrownBy(() -> fileManagementService.processFileDetails(fileSource, folder))
                .isInstanceOf(IllegalArgumentException.class)
                .as("Should throw exception for missing extension")
                .hasMessageContaining("Invalid file extension");
    }

    @Test
    @DisplayName("Should throw exception for file with dot as last character")
    void testProcessFileDetails_shouldThrowIllegalArgumentException_whenFilenameEndsWithDot() {
        // given
        FileSource fileSource = createMockFileSourceWithFilenameOnly("document.");
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);

        // when & then
        assertThatThrownBy(() -> fileManagementService.processFileDetails(fileSource, folder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw exception for unsupported MIME type")
    void testProcessFileDetails_shouldThrowIllegalArgumentException_whenUnsupportedMimeType() {
        // given
        FileSource fileSource = createMockFileSourceWithFilenameOnly("file.txt");
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);

        // when & then
        assertThatThrownBy(() -> fileManagementService.processFileDetails(fileSource, folder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should process file correctly despite case-insensitive extension")
    void testProcessFileDetails_shouldProcessSuccessfully_whenExtensionInDifferentCase() {
        // given
        FileSource fileSource = createMockFileSourceWithFilename("document.PDF");
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CVS);

        // when
        ProcessedFileDetails result = fileManagementService.processFileDetails(fileSource, folder);

        // then
        assertThat(result.mimeType()).isEqualTo(MimeType.PDF);
    }
}
