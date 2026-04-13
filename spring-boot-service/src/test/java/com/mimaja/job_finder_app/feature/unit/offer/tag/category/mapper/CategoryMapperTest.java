package com.mimaja.job_finder_app.feature.unit.offer.tag.category.mapper;

import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.TEST_CATEGORY_COLOR;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.TEST_CATEGORY_NAME;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.createTestCategory;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.createTestCategoryCreateRequestDto;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.createTestCategoryCreateRequestDtoWithDifferentColor;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.createTestCategoryWithDifferentColor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryAdminResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.mapper.CategoryMapper;
import com.mimaja.job_finder_app.feature.offer.tag.category.mapper.CategoryMapperImpl;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryMapper - Unit Tests")
class CategoryMapperTest {
    private CategoryMapper categoryMapper = new CategoryMapperImpl();

    // ==================== toEntity Tests ====================

    @Test
    @DisplayName("Should return null when mapping null CategoryCreateRequestDto")
    void testToEntity_shouldReturnNull_whenNullDtoProvided() {
        // given
        CategoryCreateRequestDto dto = null;

        // when
        Category result = categoryMapper.toEntity(dto);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName(
            "Should not be null when mapping valid CategoryCreateRequestDto to Category entity")
    void testToEntity_shouldReturnNonNullCategory_whenValidDtoProvided() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();

        // when
        Category result = categoryMapper.toEntity(dto);

        // then
        assertNotNull(result, "Category entity should not be null");
    }

    @Test
    @DisplayName(
            "Should map name correctly when mapping CategoryCreateRequestDto to Category entity")
    void testToEntity_shouldMapNameCorrectly_whenValidDtoProvided() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();

        // when
        Category result = categoryMapper.toEntity(dto);

        // then
        assertThat(result.getName()).isEqualTo(TEST_CATEGORY_NAME);
    }

    @Test
    @DisplayName(
            "Should map color correctly when mapping CategoryCreateRequestDto to Category entity")
    void testToEntity_shouldMapColorCorrectly_whenValidDtoProvided() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();

        // when
        Category result = categoryMapper.toEntity(dto);

        // then
        assertThat(result.getColor()).isEqualTo(TEST_CATEGORY_COLOR);
    }

    @Test
    @DisplayName(
            "Should map different color correctly when mapping CategoryCreateRequestDto with"
                    + " different color")
    void testToEntity_shouldMapDifferentColorCorrectly_whenDifferentColorProvided() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDtoWithDifferentColor();

        // when
        Category result = categoryMapper.toEntity(dto);

        // then
        assertThat(result.getColor()).isNotEqualTo(TEST_CATEGORY_COLOR);
    }

    @Test
    @DisplayName("Should not set id when mapping CategoryCreateRequestDto to Category entity")
    void testToEntity_shouldNotSetId_whenValidDtoProvided() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();

        // when
        Category result = categoryMapper.toEntity(dto);

        // then
        assertNull(result.getId(), "ID should not be set during mapping");
    }

    // ==================== toResponseDto Tests ====================

    @Test
    @DisplayName("Should return null when mapping null Category to CategoryResponseDto")
    void testToResponseDto_shouldReturnNull_whenNullCategoryProvided() {
        // given
        Category category = null;

        // when
        CategoryResponseDto result = categoryMapper.toResponseDto(category);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should not be null when mapping valid Category to CategoryResponseDto")
    void testToResponseDto_shouldReturnNonNullDto_whenValidCategoryProvided() {
        // given
        Category category = createTestCategory();

        // when
        CategoryResponseDto result = categoryMapper.toResponseDto(category);

        // then
        assertNotNull(result, "CategoryResponseDto should not be null");
    }

    @Test
    @DisplayName("Should map id correctly when mapping Category to CategoryResponseDto")
    void testToResponseDto_shouldMapIdCorrectly_whenValidCategoryProvided() {
        // given
        Category category = createTestCategory();

        // when
        CategoryResponseDto result = categoryMapper.toResponseDto(category);

        // then
        assertThat(result.id()).isEqualTo(category.getId());
    }

    @Test
    @DisplayName("Should map name correctly when mapping Category to CategoryResponseDto")
    void testToResponseDto_shouldMapNameCorrectly_whenValidCategoryProvided() {
        // given
        Category category = createTestCategory();

        // when
        CategoryResponseDto result = categoryMapper.toResponseDto(category);

        // then
        assertThat(result.name()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("Should map color correctly when mapping Category to CategoryResponseDto")
    void testToResponseDto_shouldMapColorCorrectly_whenValidCategoryProvided() {
        // given
        Category category = createTestCategory();

        // when
        CategoryResponseDto result = categoryMapper.toResponseDto(category);

        // then
        assertThat(result.color()).isEqualTo(category.getColor());
    }

    @Test
    @DisplayName(
            "Should map different color correctly when mapping Category with different color to"
                    + " CategoryResponseDto")
    void testToResponseDto_shouldMapDifferentColorCorrectly_whenDifferentColorProvided() {
        // given
        Category category = createTestCategoryWithDifferentColor();

        // when
        CategoryResponseDto result = categoryMapper.toResponseDto(category);

        // then
        assertThat(result.color()).isNotEqualTo(TEST_CATEGORY_COLOR);
    }

    // ==================== toAdminResponseDto Tests ====================

    @Test
    @DisplayName("Should return null when mapping null Category to CategoryAdminResponseDto")
    void testToAdminResponseDto_shouldReturnNull_whenNullCategoryProvided() {
        // given
        Category category = null;

        // when
        CategoryAdminResponseDto result = categoryMapper.toAdminResponseDto(category);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should not be null when mapping valid Category to CategoryAdminResponseDto")
    void testToAdminResponseDto_shouldReturnNonNullDto_whenValidCategoryProvided() {
        // given
        Category category = createTestCategory();

        // when
        CategoryAdminResponseDto result = categoryMapper.toAdminResponseDto(category);

        // then
        assertNotNull(result, "CategoryAdminResponseDto should not be null");
    }

    @Test
    @DisplayName("Should map id correctly when mapping Category to CategoryAdminResponseDto")
    void testToAdminResponseDto_shouldMapIdCorrectly_whenValidCategoryProvided() {
        // given
        Category category = createTestCategory();

        // when
        CategoryAdminResponseDto result = categoryMapper.toAdminResponseDto(category);

        // then
        assertThat(result.id()).isEqualTo(category.getId());
    }

    @Test
    @DisplayName("Should map name correctly when mapping Category to CategoryAdminResponseDto")
    void testToAdminResponseDto_shouldMapNameCorrectly_whenValidCategoryProvided() {
        // given
        Category category = createTestCategory();

        // when
        CategoryAdminResponseDto result = categoryMapper.toAdminResponseDto(category);

        // then
        assertThat(result.name()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("Should map color correctly when mapping Category to CategoryAdminResponseDto")
    void testToAdminResponseDto_shouldMapColorCorrectly_whenValidCategoryProvided() {
        // given
        Category category = createTestCategory();

        // when
        CategoryAdminResponseDto result = categoryMapper.toAdminResponseDto(category);

        // then
        assertThat(result.color()).isEqualTo(category.getColor());
    }

    @Test
    @DisplayName(
            "Should map different color correctly when mapping Category with different color to"
                    + " CategoryAdminResponseDto")
    void testToAdminResponseDto_shouldMapDifferentColorCorrectly_whenDifferentColorProvided() {
        // given
        Category category = createTestCategoryWithDifferentColor();

        // when
        CategoryAdminResponseDto result = categoryMapper.toAdminResponseDto(category);

        // then
        assertThat(result.color()).isNotEqualTo(TEST_CATEGORY_COLOR);
    }
}
