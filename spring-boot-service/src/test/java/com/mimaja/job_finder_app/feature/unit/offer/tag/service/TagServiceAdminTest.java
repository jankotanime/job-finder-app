package com.mimaja.job_finder_app.feature.unit.offer.tag.service;

import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.createTestTag;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.createTestTagCreateRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagService;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagServiceAdmin;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceAdminTest {
    @Mock private TagService tagService;
    @Mock private TagMapper tagMapper;
    @InjectMocks private TagServiceAdmin tagServiceAdmin;

    private Tag testTag;
    private TagResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        testTag = createTestTag();
        testResponseDto = createTestTagResponseDto();
    }

    // --- createTag ---

    @Test
    void createTag_shouldReturnNonNullResponseDto_whenTagCreated() {
        // given
        TagCreateRequestDto dto = createTestTagCreateRequestDto();
        when(tagService.createTag(dto)).thenReturn(testTag);
        when(tagMapper.toResponseDto(testTag)).thenReturn(testResponseDto);

        // when
        TagResponseDto result = tagServiceAdmin.createTag(dto);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void createTag_shouldCallTagService_whenInvoked() {
        // given
        TagCreateRequestDto dto = createTestTagCreateRequestDto();
        when(tagService.createTag(dto)).thenReturn(testTag);
        when(tagMapper.toResponseDto(any())).thenReturn(testResponseDto);

        // when
        tagServiceAdmin.createTag(dto);

        // then
        verify(tagService, times(1)).createTag(dto);
    }

    @Test
    void createTag_shouldCallMapper_whenInvoked() {
        // given
        TagCreateRequestDto dto = createTestTagCreateRequestDto();
        when(tagService.createTag(dto)).thenReturn(testTag);
        when(tagMapper.toResponseDto(testTag)).thenReturn(testResponseDto);

        // when
        tagServiceAdmin.createTag(dto);

        // then
        verify(tagMapper, times(1)).toResponseDto(testTag);
    }

    // --- updateTag ---

    @Test
    void updateTag_shouldReturnNonNullResponseDto_whenTagUpdated() {
        // given
        UUID tagId = testTag.getId();
        TagCreateRequestDto dto = createTestTagCreateRequestDto();
        when(tagService.updateTag(tagId, dto)).thenReturn(testTag);
        when(tagMapper.toResponseDto(testTag)).thenReturn(testResponseDto);

        // when
        TagResponseDto result = tagServiceAdmin.updateTag(tagId, dto);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void updateTag_shouldCallTagService_whenInvoked() {
        // given
        UUID tagId = testTag.getId();
        TagCreateRequestDto dto = createTestTagCreateRequestDto();
        when(tagService.updateTag(tagId, dto)).thenReturn(testTag);
        when(tagMapper.toResponseDto(any())).thenReturn(testResponseDto);

        // when
        tagServiceAdmin.updateTag(tagId, dto);

        // then
        verify(tagService, times(1)).updateTag(tagId, dto);
    }

    // --- deleteTag ---

    @Test
    void deleteTag_shouldCallTagService_whenInvoked() {
        // given
        UUID tagId = testTag.getId();

        // when
        tagServiceAdmin.deleteTag(tagId);

        // then
        verify(tagService, times(1)).deleteTag(tagId);
    }

    private TagResponseDto createTestTagResponseDto() {
        return new TagResponseDto(testTag.getId(), testTag.getName(), null, null);
    }
}
