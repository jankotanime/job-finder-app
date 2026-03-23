package com.mimaja.job_finder_app.feature.unit.shared.mockdata;

import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import com.mimaja.job_finder_app.shared.interfaces.FileSource;
import org.mockito.Mockito;

public class FileManagementMockData {

    public static final String TEST_FILE_NAME = "test-document.pdf";
    public static final String TEST_CONTENT_TYPE = "application/pdf";
    public static final String TEST_STORAGE_KEY = "documents/cvs/12345678-1234-1234-1234-123456789012-test-document.pdf";
    public static final long TEST_FILE_SIZE = 2048L;
    public static final String TEST_BUCKET = "test-bucket";
    public static final String TEST_FOLDER = "documents/cvs";

    public static final String TEST_PHOTO_NAME = "test-photo.jpg";
    public static final String TEST_PHOTO_CONTENT_TYPE = "image/jpeg";
    public static final String TEST_PHOTO_STORAGE_KEY = "photos/offer-photos/87654321-4321-4321-4321-210987654321-test-photo.jpg";
    public static final long TEST_PHOTO_SIZE = 4096L;
    public static final String TEST_PHOTO_FOLDER = "photos/offer-photos";

    public static ProcessedFileDetails createTestProcessedFileDetails() {
        return new ProcessedFileDetails(
                TEST_FILE_NAME,
                TEST_CONTENT_TYPE,
                MimeType.PDF,
                TEST_STORAGE_KEY,
                TEST_FILE_SIZE,
                new byte[]{1, 2, 3, 4});
    }

    public static ProcessedFileDetails createTestProcessedFileDetailsWithMimeType(MimeType mimeType) {
        return new ProcessedFileDetails(
                TEST_FILE_NAME,
                TEST_CONTENT_TYPE,
                mimeType,
                TEST_STORAGE_KEY,
                TEST_FILE_SIZE,
                new byte[]{1, 2, 3, 4});
    }

    public static ProcessedFileDetails createTestProcessedFileDetailsForPhoto() {
        return new ProcessedFileDetails(
                TEST_PHOTO_NAME,
                TEST_PHOTO_CONTENT_TYPE,
                MimeType.JPG,
                TEST_PHOTO_STORAGE_KEY,
                TEST_PHOTO_SIZE,
                new byte[]{5, 6, 7, 8});
    }

    public static FileSource createMockFileSource() {
        FileSource fileSource = Mockito.mock(FileSource.class);
        Mockito.when(fileSource.getOriginalFilename()).thenReturn(TEST_FILE_NAME);
        Mockito.when(fileSource.getContentType()).thenReturn(TEST_CONTENT_TYPE);
        Mockito.when(fileSource.getSize()).thenReturn(TEST_FILE_SIZE);
        Mockito.when(fileSource.getBytes()).thenReturn(new byte[]{1, 2, 3, 4});
        return fileSource;
    }

    public static FileSource createMockFileSourceWithFilename(String filename) {
        FileSource fileSource = Mockito.mock(FileSource.class);
        Mockito.when(fileSource.getOriginalFilename()).thenReturn(filename);
        Mockito.when(fileSource.getContentType()).thenReturn(TEST_CONTENT_TYPE);
        Mockito.when(fileSource.getSize()).thenReturn(TEST_FILE_SIZE);
        Mockito.when(fileSource.getBytes()).thenReturn(new byte[]{1, 2, 3, 4});
        return fileSource;
    }

    public static FileSource createMockFileSourceWithFilenameOnly(String filename) {
        FileSource fileSource = Mockito.mock(FileSource.class);
        Mockito.when(fileSource.getOriginalFilename()).thenReturn(filename);
        return fileSource;
    }

    public static FileSource createMockFileSourceForPhoto() {
        FileSource fileSource = Mockito.mock(FileSource.class);
        Mockito.when(fileSource.getOriginalFilename()).thenReturn(TEST_PHOTO_NAME);
        Mockito.when(fileSource.getContentType()).thenReturn(TEST_PHOTO_CONTENT_TYPE);
        Mockito.when(fileSource.getSize()).thenReturn(TEST_PHOTO_SIZE);
        Mockito.when(fileSource.getBytes()).thenReturn(new byte[]{5, 6, 7, 8});
        return fileSource;
    }
}
