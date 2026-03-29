package com.mimaja.job_finder_app.feature.unit.offer.tag.service;

import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.createTestTag;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagService;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagServiceUser;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TagServiceUserTest {
    @Mock private TagService tagService;
    @Mock private TagMapper tagMapper;
    @InjectMocks private TagServiceUser tagServiceUser;

    private Tag testTag;
    private TagResponseDto testResponseDto;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testTag = createTestTag();
        testResponseDto = new TagResponseDto(testTag.getId(), testTag.getName(), null, null);
        testPageable = PageRequest.of(0, 10);
    }

    // --- getAllTags ---

    @Test
    void getAllTags_shouldReturnMappedPage_whenTagsExist() {
        // given
        Page<Tag> tagPage = new PageImpl<>(List.of(testTag), testPageable, 1);
        when(tagService.getAllTags(any(), any())).thenReturn(tagPage);
        when(tagMapper.toResponseDto(testTag)).thenReturn(testResponseDto);

        // when
        Page<TagResponseDto> result =
                tagServiceUser.getAllTags(new TagFilterRequestDto(null, null), testPageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getAllTags_shouldCallTagService_whenInvoked() {
        // given
        Page<Tag> emptyPage = new PageImpl<>(List.of(), testPageable, 0);
        when(tagService.getAllTags(any(), any())).thenReturn(emptyPage);

        // when
        tagServiceUser.getAllTags(new TagFilterRequestDto(null, null), testPageable);

        // then
        verify(tagService, times(1)).getAllTags(any(), any());
    }

    // --- getAllByCategoryId ---

    @Test
    void getAllByCategoryId_shouldReturnMappedPage_whenTagsExist() {
        // given
        UUID categoryId = UUID.randomUUID();
        Page<Tag> tagPage = new PageImpl<>(List.of(testTag), testPageable, 1);
        when(tagService.getAllByCategoryId(categoryId, testPageable)).thenReturn(tagPage);
        when(tagMapper.toResponseDto(testTag)).thenReturn(testResponseDto);

        // when
        Page<TagResponseDto> result = tagServiceUser.getAllByCategoryId(categoryId, testPageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getAllByCategoryId_shouldCallTagService_whenInvoked() {
        // given
        UUID categoryId = UUID.randomUUID();
        when(tagService.getAllByCategoryId(categoryId, testPageable))
                .thenReturn(new PageImpl<>(List.of()));

        // when
        tagServiceUser.getAllByCategoryId(categoryId, testPageable);

        // then
        verify(tagService, times(1)).getAllByCategoryId(categoryId, testPageable);
    }

    // --- getTagById ---

    @Test
    void getTagById_shouldReturnNonNullResponseDto_whenTagExists() {
        // given
        UUID tagId = testTag.getId();
        when(tagService.getTagById(tagId)).thenReturn(testTag);
        when(tagMapper.toResponseDto(testTag)).thenReturn(testResponseDto);

        // when
        TagResponseDto result = tagServiceUser.getTagById(tagId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void getTagById_shouldCallTagService_whenInvoked() {
        // given
        UUID tagId = testTag.getId();
        when(tagService.getTagById(tagId)).thenReturn(testTag);
        when(tagMapper.toResponseDto(any())).thenReturn(testResponseDto);

        // when
        tagServiceUser.getTagById(tagId);

        // then
        verify(tagService, times(1)).getTagById(tagId);
    }
}
