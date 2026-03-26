package com.mimaja.job_finder_app.feature.unit.shared.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.shared.adapters.R2FileSource;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@ExtendWith(MockitoExtension.class)
class R2FileSourceTest {
    private static final String TEST_FILENAME = "test-document.pdf";
    private static final String TEST_CONTENT_TYPE = "application/pdf";
    private static final long TEST_CONTENT_LENGTH = 2048L;

    @Mock private ResponseInputStream<GetObjectResponse> response;
    @Mock private GetObjectResponse getObjectResponse;

    private R2FileSource fileSource;

    @BeforeEach
    void setUp() {
        fileSource = new R2FileSource(response, TEST_FILENAME);
    }

    // --- getInputStream ---

    @Test
    void getInputStream_shouldReturnResponse_whenInvoked() {
        // when
        InputStream result = fileSource.getInputStream();

        // then
        assertThat(result).isEqualTo(response);
    }

    // --- getOriginalFilename ---

    @Test
    void getOriginalFilename_shouldReturnFilename_whenInvoked() {
        // when
        String result = fileSource.getOriginalFilename();

        // then
        assertThat(result).isEqualTo(TEST_FILENAME);
    }

    // --- getContentType ---

    @Test
    void getContentType_shouldReturnContentType_whenResponseHasContentType() {
        // given
        when(response.response()).thenReturn(getObjectResponse);
        when(getObjectResponse.contentType()).thenReturn(TEST_CONTENT_TYPE);

        // when
        String result = fileSource.getContentType();

        // then
        assertThat(result).isEqualTo(TEST_CONTENT_TYPE);
    }

    // --- getSize ---

    @Test
    void getSize_shouldReturnContentLength_whenResponseHasContentLength() {
        // given
        when(response.response()).thenReturn(getObjectResponse);
        when(getObjectResponse.contentLength()).thenReturn(TEST_CONTENT_LENGTH);

        // when
        long result = fileSource.getSize();

        // then
        assertThat(result).isEqualTo(TEST_CONTENT_LENGTH);
    }

    // --- getBytes ---

    @Test
    void getBytes_shouldReturnBytes_whenResponseIsReadable() throws IOException {
        // given
        byte[] expectedBytes = {1, 2, 3, 4};
        when(response.readAllBytes()).thenReturn(expectedBytes);

        // when
        byte[] result = fileSource.getBytes();

        // then
        assertThat(result).isEqualTo(expectedBytes);
    }

    @Test
    void getBytes_shouldThrowApplicationException_whenIOExceptionOccurs() throws IOException {
        // given
        when(response.readAllBytes()).thenThrow(new IOException("read error"));

        // when / then
        assertThrows(ApplicationException.class, () -> fileSource.getBytes());
    }
}
