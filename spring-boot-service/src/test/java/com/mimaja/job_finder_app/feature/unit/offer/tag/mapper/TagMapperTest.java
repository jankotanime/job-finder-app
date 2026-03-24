package com.mimaja.job_finder_app.feature.unit.offer.tag.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.createTestTag;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.createTestTagWithCategory;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.createTestTagWithoutCategory;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.createTestTagCreateRequestDto;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.createTestTagSet;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.createTestTagSetWithCategories;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.TEST_TAG_NAME;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.TEST_CATEGORY_NAME;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.TEST_CATEGORY_COLOR;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapperImpl;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;

@DisplayName("TagMapper - Unit Tests")
class TagMapperTest {

    private final TagMapper tagMapper = new TagMapperImpl();

    // ==================== toEntity Tests ====================

    @Test
    @DisplayName("Should return null when mapping null TagCreateRequestDto to Tag")
    void testToEntity_shouldReturnNull_whenNullDtoProvided() {
        // given
        TagCreateRequestDto requestDto = null;

        // when
        Tag result = tagMapper.toEntity(requestDto);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should not be null when mapping valid TagCreateRequestDto to Tag")
    void testToEntity_shouldReturnNonNullTag_whenValidDtoProvided() {
        // given
        TagCreateRequestDto requestDto = createTestTagCreateRequestDto();

        // when
        Tag result = tagMapper.toEntity(requestDto);

        // then
        assertNotNull(result, "Tag should not be null");
    }

    @Test
    @DisplayName("Should map name correctly when mapping TagCreateRequestDto to Tag")
    void testToEntity_shouldMapNameCorrectly_whenValidDtoProvided() {
        // given
        TagCreateRequestDto requestDto = createTestTagCreateRequestDto();

        // when
        Tag result = tagMapper.toEntity(requestDto);

        // then
        assertThat(result.getName()).isEqualTo(TEST_TAG_NAME);
    }

    // ==================== toResponseDto Tests ====================

    @Test
    @DisplayName("Should return null when mapping null Tag to TagResponseDto")
    void testToResponseDto_shouldReturnNull_whenNullTagProvided() {
        // given
        Tag tag = null;

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should not be null when mapping valid Tag to TagResponseDto")
    void testToResponseDto_shouldReturnNonNullDto_whenValidTagProvided() {
        // given
        Tag tag = createTestTag();

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertNotNull(result, "TagResponseDto should not be null");
    }

    @Test
    @DisplayName("Should map id correctly when mapping Tag to TagResponseDto")
    void testToResponseDto_shouldMapIdCorrectly_whenValidTagProvided() {
        // given
        Tag tag = createTestTag();

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertThat(result.id()).isEqualTo(tag.getId());
    }

    @Test
    @DisplayName("Should map name correctly when mapping Tag to TagResponseDto")
    void testToResponseDto_shouldMapNameCorrectly_whenValidTagProvided() {
        // given
        Tag tag = createTestTag();

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertThat(result.name()).isEqualTo(tag.getName());
    }

    @Test
    @DisplayName("Should map categoryName correctly when tag has category")
    void testToResponseDto_shouldMapCategoryNameCorrectly_whenTagHasCategory() {
        // given
        Tag tag = createTestTagWithCategory();

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertThat(result.categoryName()).isEqualTo(TEST_CATEGORY_NAME);
    }

    @Test
    @DisplayName("Should map categoryName to null when tag has no category")
    void testToResponseDto_shouldMapCategoryNameToNull_whenTagHasNoCategory() {
        // given
        Tag tag = createTestTagWithoutCategory();

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertNull(result.categoryName(), "Category name should be null when tag has no category");
    }

    @Test
    @DisplayName("Should map categoryColor correctly when tag has category")
    void testToResponseDto_shouldMapCategoryColorCorrectly_whenTagHasCategory() {
        // given
        Tag tag = createTestTagWithCategory();

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertThat(result.categoryColor()).isEqualTo(TEST_CATEGORY_COLOR);
    }

    @Test
    @DisplayName("Should map categoryColor to null when tag has no category")
    void testToResponseDto_shouldMapCategoryColorToNull_whenTagHasNoCategory() {
        // given
        Tag tag = createTestTagWithoutCategory();

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertNull(result.categoryColor(), "Category color should be null when tag has no category");
    }

    // ==================== toSetOfResponseDto Tests ====================

    @Test
    @DisplayName("Should return null when mapping null Set of tags")
    void testToSetOfResponseDto_shouldReturnNull_whenNullSetProvided() {
        // given
        Set<Tag> tags = null;

        // when
        Set<TagResponseDto> result = tagMapper.toSetOfResponseDto(tags);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should not be null when mapping valid Set of tags")
    void testToSetOfResponseDto_shouldReturnNonNullSet_whenValidSetProvided() {
        // given
        Set<Tag> tags = createTestTagSet();

        // when
        Set<TagResponseDto> result = tagMapper.toSetOfResponseDto(tags);

        // then
        assertNotNull(result, "Set should not be null");
    }

    @Test
    @DisplayName("Should return empty set when mapping empty set of tags")
    void testToSetOfResponseDto_shouldReturnEmptySet_whenEmptySetProvided() {
        // given
        Set<Tag> tags = new HashSet<>();

        // when
        Set<TagResponseDto> result = tagMapper.toSetOfResponseDto(tags);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should map all tags correctly when mapping Set of tags")
    void testToSetOfResponseDto_shouldMapAllTagsCorrectly_whenValidSetProvided() {
        // given
        Set<Tag> tags = createTestTagSet();

        // when
        Set<TagResponseDto> result = tagMapper.toSetOfResponseDto(tags);

        // then
        assertThat(result).hasSize(tags.size());
    }

    @Test
    @DisplayName("Should map tags with categories correctly when mapping Set of tags with categories")
    void testToSetOfResponseDto_shouldMapTagsWithCategoriesCorrectly_whenSetHasTagsWithCategories() {
        // given
        Set<Tag> tags = createTestTagSetWithCategories();

        // when
        Set<TagResponseDto> result = tagMapper.toSetOfResponseDto(tags);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("Should handle tags without categories in Set mapping")
    void testToSetOfResponseDto_shouldHandleTagsWithoutCategories_whenSetHasTagsWithoutCategories() {
        // given
        Set<Tag> tags = new HashSet<>();
        tags.add(createTestTagWithoutCategory());

        // when
        Set<TagResponseDto> result = tagMapper.toSetOfResponseDto(tags);

        // then
        assertThat(result).hasSize(1);
    }

    // ==================== tagCategoryName Helper Tests ====================

    @Test
    @DisplayName("Should return category name when tag has category")
    void testTagCategoryName_shouldReturnCategoryName_whenCategoryExists() {
        // given
        Tag tag = createTestTagWithCategory();

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertThat(result.categoryName()).isNotNull();
    }

    @Test
    @DisplayName("Should return null category name when tag has no category")
    void testTagCategoryName_shouldReturnNull_whenCategoryIsNull() {
        // given
        Tag tag = createTestTag();
        tag.setCategory(null);

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertNull(result.categoryName(), "Category name should be null");
    }

    // ==================== tagCategoryColor Helper Tests ====================

    @Test
    @DisplayName("Should return category color when tag has category")
    void testTagCategoryColor_shouldReturnCategoryColor_whenCategoryExists() {
        // given
        Tag tag = createTestTagWithCategory();

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertThat(result.categoryColor()).isNotNull();
    }

    @Test
    @DisplayName("Should return null category color when tag has no category")
    void testTagCategoryColor_shouldReturnNull_whenCategoryIsNull() {
        // given
        Tag tag = createTestTag();
        tag.setCategory(null);

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertNull(result.categoryColor(), "Category color should be null");
    }

    @Test
    @DisplayName("Should map correct category color when category has specific color")
    void testTagCategoryColor_shouldMapCorrectColor_whenCategoryHasSpecificColor() {
        // given
        Tag tag = createTestTagWithCategory();

        // when
        TagResponseDto result = tagMapper.toResponseDto(tag);

        // then
        assertThat(result.categoryColor()).isEqualTo(TEST_CATEGORY_COLOR);
    }
}
