package com.mimaja.job_finder_app.feature.unit.cv.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.createTestCv;
import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.createTestCvUploadRequestDto;
import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.createTestCvUploadRequestDtoWithNullFileName;
import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.createTestCvUploadRequestDtoWithNullMimeType;
import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.createTestCvUploadRequestDtoWithNullStorageKey;
import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.createTestCvWithNullStorageKey;
import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.TEST_CV_FILENAME;
import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.TEST_CV_STORAGE_KEY;
import static com.mimaja.job_finder_app.feature.unit.cv.mockdata.CvMockData.TEST_CV_FILE_SIZE;
import static com.mimaja.job_finder_app.feature.unit.user.mockdata.UserMockData.createTestUser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mimaja.job_finder_app.feature.cv.dto.CvResponseDto;
import com.mimaja.job_finder_app.feature.cv.dto.CvUploadRequestDto;
import com.mimaja.job_finder_app.feature.cv.mapper.CvMapper;
import com.mimaja.job_finder_app.feature.cv.mapper.CvMapperImpl;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.shared.enums.MimeType;

@ExtendWith(MockitoExtension.class)
@DisplayName("CvMapper - Unit Tests")
class CvMapperTest {

    private CvMapper cvMapper;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        cvMapper = new CvMapperImpl();
    }

    // ==================== toEntity Tests ====================

    @Test
    @DisplayName("Should return null when mapping null CvUploadRequestDto")
    void testToEntity_shouldReturnNull_whenNullDtoProvided() {
        // given
        CvUploadRequestDto dto = null;

        // when
        Cv result = cvMapper.toEntity(dto);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should not be null when mapping valid CvUploadRequestDto")
    void testToEntity_shouldReturnNonNullCv_whenValidDtoProvided() {
        // given
        CvUploadRequestDto dto = createTestCvUploadRequestDto(createTestUser());

        // when
        Cv result = cvMapper.toEntity(dto);

        // then
        assertNotNull(result, "Cv should not be null");
    }

    @Test
    @DisplayName("Should map fileName correctly when mapping CvUploadRequestDto")
    void testToEntity_shouldMapFileNameCorrectly_whenValidDtoProvided() {
        // given
        CvUploadRequestDto dto = createTestCvUploadRequestDto(createTestUser());

        // when
        Cv result = cvMapper.toEntity(dto);

        // then
        assertThat(result.getFileName()).isEqualTo(TEST_CV_FILENAME);
    }

    @Test
    @DisplayName("Should map mimeType correctly when mapping CvUploadRequestDto")
    void testToEntity_shouldMapMimeTypeCorrectly_whenValidDtoProvided() {
        // given
        CvUploadRequestDto dto = createTestCvUploadRequestDto(createTestUser());

        // when
        Cv result = cvMapper.toEntity(dto);

        // then
        assertThat(result.getMimeType()).isEqualTo(MimeType.PDF);
    }

    @Test
    @DisplayName("Should map fileSize correctly when mapping CvUploadRequestDto")
    void testToEntity_shouldMapFileSizeCorrectly_whenValidDtoProvided() {
        // given
        CvUploadRequestDto dto = createTestCvUploadRequestDto(createTestUser());

        // when
        Cv result = cvMapper.toEntity(dto);

        // then
        assertThat(result.getFileSize()).isEqualTo(TEST_CV_FILE_SIZE);
    }

    @Test
    @DisplayName("Should map storageKey correctly when mapping CvUploadRequestDto")
    void testToEntity_shouldMapStorageKeyCorrectly_whenValidDtoProvided() {
        // given
        CvUploadRequestDto dto = createTestCvUploadRequestDto(createTestUser());

        // when
        Cv result = cvMapper.toEntity(dto);

        // then
        assertThat(result.getStorageKey()).isEqualTo(TEST_CV_STORAGE_KEY);
    }

    @Test
    @DisplayName("Should handle null fileName when mapping CvUploadRequestDto")
    void testToEntity_shouldHandleNullFileName_whenFileNameIsNull() {
        // given
        CvUploadRequestDto dto = createTestCvUploadRequestDtoWithNullFileName(createTestUser());

        // when
        Cv result = cvMapper.toEntity(dto);

        // then
        assertThat(result.getFileName()).isNull();
    }

    @Test
    @DisplayName("Should handle null mimeType when mapping CvUploadRequestDto")
    void testToEntity_shouldHandleNullMimeType_whenMimeTypeIsNull() {
        // given
        CvUploadRequestDto dto = createTestCvUploadRequestDtoWithNullMimeType(createTestUser());

        // when
        Cv result = cvMapper.toEntity(dto);

        // then
        assertThat(result.getMimeType()).isNull();
    }

    @Test
    @DisplayName("Should handle null storageKey when mapping CvUploadRequestDto")
    void testToEntity_shouldHandleNullStorageKey_whenStorageKeyIsNull() {
        // given
        CvUploadRequestDto dto = createTestCvUploadRequestDtoWithNullStorageKey(createTestUser());

        // when
        Cv result = cvMapper.toEntity(dto);

        // then
        assertThat(result.getStorageKey()).isNull();
    }

    // ==================== toResponseDto Tests ====================

    @Test
    @DisplayName("Should return null when mapping null Cv")
    void testToResponseDto_shouldReturnNull_whenNullCvProvided() {
        // given
        Cv cv = null;

        // when
        CvResponseDto result = cvMapper.toResponseDto(cv);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should not be null when mapping valid Cv")
    void testToResponseDto_shouldReturnNonNullDto_whenValidCvProvided() {
        // given
        Cv cv = createTestCv();

        // when
        CvResponseDto result = cvMapper.toResponseDto(cv);

        // then
        assertNotNull(result, "CvResponseDto should not be null");
    }

    @Test
    @DisplayName("Should map id correctly when mapping Cv to CvResponseDto")
    void testToResponseDto_shouldMapIdCorrectly_whenValidCvProvided() {
        // given
        Cv cv = createTestCv();

        // when
        CvResponseDto result = cvMapper.toResponseDto(cv);

        // then
        assertThat(result.id()).isEqualTo(cv.getId());
    }

    @Test
    @DisplayName("Should map storageKey correctly when mapping Cv to CvResponseDto")
    void testToResponseDto_shouldMapStorageKeyCorrectly_whenValidCvProvided() {
        // given
        Cv cv = createTestCv();

        // when
        CvResponseDto result = cvMapper.toResponseDto(cv);

        // then
        assertThat(result.storageKey()).isEqualTo(TEST_CV_STORAGE_KEY);
    }

    @Test
    @DisplayName("Should handle null storageKey when mapping Cv to CvResponseDto")
    void testToResponseDto_shouldHandleNullStorageKey_whenStorageKeyIsNull() {
        // given
        Cv cv = createTestCvWithNullStorageKey();

        // when
        CvResponseDto result = cvMapper.toResponseDto(cv);

        // then
        assertThat(result.storageKey()).isNull();
    }

    @Test
    @DisplayName("Should not include fileName in response when mapping Cv to CvResponseDto")
    void testToResponseDto_shouldNotMapFileName_whenMappingCvToResponseDto() {
        // given
        Cv cv = createTestCv();

        // when
        CvResponseDto result = cvMapper.toResponseDto(cv);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should not include mimeType in response when mapping Cv to CvResponseDto")
    void testToResponseDto_shouldNotMapMimeType_whenMappingCvToResponseDto() {
        // given
        Cv cv = createTestCv();

        // when
        CvResponseDto result = cvMapper.toResponseDto(cv);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should not include fileSize in response when mapping Cv to CvResponseDto")
    void testToResponseDto_shouldNotMapFileSize_whenMappingCvToResponseDto() {
        // given
        Cv cv = createTestCv();

        // when
        CvResponseDto result = cvMapper.toResponseDto(cv);

        // then
        assertThat(result).isNotNull();
    }
}
