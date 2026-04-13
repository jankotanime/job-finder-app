package com.mimaja.job_finder_app.feature.unit.cv.model;

import static com.mimaja.job_finder_app.feature.unit.security.mockdata.SecurityMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.mimaja.job_finder_app.feature.cv.dto.CvUpdateRequestDto;
import com.mimaja.job_finder_app.feature.cv.dto.CvUploadRequestDto;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import com.mimaja.job_finder_app.shared.model.FileBase;
import org.junit.jupiter.api.Test;

class CvModelTest {
    private static final String FILE_NAME = "cv.pdf";
    private static final String STORAGE_KEY = "key-1";
    private static final long FILE_SIZE = 10L;
    private static final String OLD_FILE_NAME = "old.pdf";
    private static final String OLD_STORAGE_KEY = "old";
    private static final long OLD_FILE_SIZE = 1L;

    @Test
    void from_shouldReturnNull_whenFileBaseIsNull() {
        // when
        Cv result = Cv.from((FileBase) null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void from_shouldCopyFileName_whenFileBaseIsNonNull() {
        // given
        Cv source =
                Cv.builder()
                        .fileName(FILE_NAME)
                        .mimeType(MimeType.PDF)
                        .fileSize(FILE_SIZE)
                        .storageKey(STORAGE_KEY)
                        .build();

        // when
        Cv result = Cv.from((FileBase) source);

        // then
        assertThat(result.getFileName()).isEqualTo(FILE_NAME);
    }

    @Test
    void fromUploadDto_shouldSetUserAndFileFields_whenDtoProvided() {
        // given
        var user = createTestUser();
        CvUploadRequestDto dto =
                new CvUploadRequestDto(FILE_NAME, MimeType.PDF, FILE_SIZE, STORAGE_KEY, user);

        // when
        Cv result = Cv.from(dto);

        // then
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    void update_shouldSetStorageKeyFromDto_whenDtoProvided() {
        // given
        Cv cv = new Cv();
        cv.setFileName(OLD_FILE_NAME);
        cv.setMimeType(MimeType.PDF);
        cv.setFileSize(OLD_FILE_SIZE);
        cv.setStorageKey(OLD_STORAGE_KEY);
        CvUpdateRequestDto dto =
                new CvUpdateRequestDto(FILE_NAME, MimeType.PDF, FILE_SIZE, STORAGE_KEY);

        // when
        cv.update(dto);

        // then
        assertThat(cv.getStorageKey()).isEqualTo(STORAGE_KEY);
    }
}
