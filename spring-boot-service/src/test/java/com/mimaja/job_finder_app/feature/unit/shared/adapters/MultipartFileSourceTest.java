package com.mimaja.job_finder_app.feature.unit.shared.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.shared.adapters.MultipartFileSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MultipartFileSourceTest {
    private static final String TEST_FILENAME = "test_file.PDF";
    private static final String TEST_FILENAME_LOWERCASE = "test_file.pdf";
    private static final String TEST_CONTENT_TYPE = "application/pdf";
    private static final long TEST_FILE_SIZE = 2048L;

    @Mock private MultipartFile multipartFile;

    private MultipartFileSource fileSource;

    @BeforeEach
    void setUp() {
        fileSource = new MultipartFileSource(multipartFile);
    }

    // --- getInputStream ---

    @Test
    void getInputStream_shouldReturnNonNullStream_whenFileIsReadable() throws IOException {
        // given
        InputStream stream = new ByteArrayInputStream(new byte[] {1, 2, 3});
        when(multipartFile.getInputStream()).thenReturn(stream);

        // when
        InputStream result = fileSource.getInputStream();

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void getInputStream_shouldThrowApplicationException_whenIOExceptionOccurs() throws IOException {
        // given
        when(multipartFile.getInputStream()).thenThrow(new IOException("test error"));

        // when / then
        assertThrows(ApplicationException.class, () -> fileSource.getInputStream());
    }

    // --- getOriginalFilename ---

    @Test
    void getOriginalFilename_shouldReturnLowercaseFilename_whenFilenamePresent() {
        // given
        when(multipartFile.getOriginalFilename()).thenReturn(TEST_FILENAME);

        // when
        String result = fileSource.getOriginalFilename();

        // then
        assertThat(result).isEqualTo(TEST_FILENAME_LOWERCASE);
    }

    @Test
    void getOriginalFilename_shouldThrowApplicationException_whenFilenameIsNull() {
        // given
        when(multipartFile.getOriginalFilename()).thenReturn(null);

        // when / then
        assertThrows(ApplicationException.class, () -> fileSource.getOriginalFilename());
    }

    // --- getContentType ---

    @Test
    void getContentType_shouldReturnContentType_whenContentTypePresent() {
        // given
        when(multipartFile.getContentType()).thenReturn(TEST_CONTENT_TYPE);

        // when
        String result = fileSource.getContentType();

        // then
        assertThat(result).isEqualTo(TEST_CONTENT_TYPE);
    }

    @Test
    void getContentType_shouldThrowApplicationException_whenContentTypeIsNull() {
        // given
        when(multipartFile.getContentType()).thenReturn(null);

        // when / then
        assertThrows(ApplicationException.class, () -> fileSource.getContentType());
    }

    // --- getSize ---

    @Test
    void getSize_shouldReturnFileSize_whenInvoked() {
        // given
        when(multipartFile.getSize()).thenReturn(TEST_FILE_SIZE);

        // when
        long result = fileSource.getSize();

        // then
        assertThat(result).isEqualTo(TEST_FILE_SIZE);
    }

    // --- getBytes ---

    @Test
    void getBytes_shouldReturnBytes_whenFileIsReadable() throws IOException {
        // given
        byte[] expectedBytes = {1, 2, 3, 4};
        when(multipartFile.getBytes()).thenReturn(expectedBytes);

        // when
        byte[] result = fileSource.getBytes();

        // then
        assertThat(result).isEqualTo(expectedBytes);
    }

    @Test
    void getBytes_shouldThrowApplicationException_whenIOExceptionOccurs() throws IOException {
        // given
        when(multipartFile.getBytes()).thenThrow(new IOException("test error"));

        // when / then
        assertThrows(ApplicationException.class, () -> fileSource.getBytes());
    }
}
