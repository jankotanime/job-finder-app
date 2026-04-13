package com.mimaja.job_finder_app.feature.unit.offer.tag.service;

import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.TEST_TAG_NAME;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.createTestTag;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata.TagMockData.createTestTagCreateRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryService;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.offer.tag.repository.TagRepository;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagServiceDefault;
import java.util.List;
import java.util.Optional;
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
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class TagServiceDefaultTest {
    @Mock private TagRepository tagRepository;
    @Mock private CategoryService categoryService;
    @Mock private TagMapper tagMapper;
    @InjectMocks private TagServiceDefault tagService;

    private Tag testTag;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testTag = createTestTag();
        testPageable = PageRequest.of(0, 10);
    }

    // --- getAllTags ---

    @Test
    @SuppressWarnings("unchecked")
    void getAllTags_shouldReturnPage_whenTagsExist() {
        // given
        Page<Tag> expectedPage = new PageImpl<>(List.of(testTag), testPageable, 1);
        when(tagRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        // when
        Page<Tag> result = tagService.getAllTags(new TagFilterRequestDto(null, null), testPageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAllTags_shouldCallRepository_whenInvoked() {
        // given
        Page<Tag> emptyPage = new PageImpl<>(List.of(), testPageable, 0);
        when(tagRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        // when
        tagService.getAllTags(new TagFilterRequestDto(null, null), testPageable);

        // then
        verify(tagRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    // --- getAllByCategoryId ---

    @Test
    void getAllByCategoryId_shouldReturnPage_whenTagsExist() {
        // given
        UUID categoryId = UUID.randomUUID();
        Page<Tag> expectedPage = new PageImpl<>(List.of(testTag), testPageable, 1);
        when(tagRepository.getAllByCategoryId(categoryId, testPageable)).thenReturn(expectedPage);

        // when
        Page<Tag> result = tagService.getAllByCategoryId(categoryId, testPageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getAllByCategoryId_shouldCallRepository_whenInvoked() {
        // given
        UUID categoryId = UUID.randomUUID();
        when(tagRepository.getAllByCategoryId(categoryId, testPageable))
                .thenReturn(new PageImpl<>(List.of()));

        // when
        tagService.getAllByCategoryId(categoryId, testPageable);

        // then
        verify(tagRepository, times(1)).getAllByCategoryId(categoryId, testPageable);
    }

    // --- getTagById ---

    @Test
    void getTagById_shouldReturnTag_whenTagExists() {
        // given
        UUID tagId = testTag.getId();
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));

        // when
        Tag result = tagService.getTagById(tagId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void getTagById_shouldReturnCorrectTag_whenTagExists() {
        // given
        UUID tagId = testTag.getId();
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));

        // when
        Tag result = tagService.getTagById(tagId);

        // then
        assertThat(result.getId()).isEqualTo(tagId);
    }

    @Test
    void getTagById_shouldThrowExceptionWithTagNotFoundCode_whenTagNotFound() {
        // given
        UUID tagId = UUID.randomUUID();
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> tagService.getTagById(tagId));

        // then
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.TAG_NOT_FOUND.getCode());
    }

    // --- createTag ---

    @Test
    void createTag_shouldReturnSavedTag_whenTagNameNotExists() {
        // given
        TagCreateRequestDto dto = createTestTagCreateRequestDto();
        when(tagRepository.findByName(dto.name())).thenReturn(Optional.empty());
        when(categoryService.getCategoryById(dto.categoryId())).thenReturn(createTestCategory());
        when(tagMapper.toEntity(dto)).thenReturn(testTag);
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        // when
        Tag result = tagService.createTag(dto);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void createTag_shouldCallRepositorySave_whenTagNameNotExists() {
        // given
        TagCreateRequestDto dto = createTestTagCreateRequestDto();
        when(tagRepository.findByName(dto.name())).thenReturn(Optional.empty());
        when(categoryService.getCategoryById(dto.categoryId())).thenReturn(createTestCategory());
        when(tagMapper.toEntity(dto)).thenReturn(testTag);
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        // when
        tagService.createTag(dto);

        // then
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void createTag_shouldThrowExceptionWithTagAlreadyExistsCode_whenTagNameExists() {
        // given
        TagCreateRequestDto dto = createTestTagCreateRequestDto();
        when(tagRepository.findByName(dto.name())).thenReturn(Optional.of(testTag));

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> tagService.createTag(dto));

        // then
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.TAG_ALREADY_EXISTS.getCode());
    }

    @Test
    void createTag_shouldNotCallRepositorySave_whenTagNameExists() {
        // given
        TagCreateRequestDto dto = createTestTagCreateRequestDto();
        when(tagRepository.findByName(dto.name())).thenReturn(Optional.of(testTag));

        // when
        assertThrows(BusinessException.class, () -> tagService.createTag(dto));

        // then
        verify(tagRepository, times(0)).save(any(Tag.class));
    }

    // --- updateTag ---

    @Test
    void updateTag_shouldReturnUpdatedTag_whenTagExistsAndNameNotChanged() {
        // given
        UUID tagId = testTag.getId();
        TagCreateRequestDto dto = new TagCreateRequestDto(TEST_TAG_NAME, UUID.randomUUID());
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));
        when(categoryService.getCategoryById(dto.categoryId())).thenReturn(createTestCategory());
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        // when
        Tag result = tagService.updateTag(tagId, dto);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void updateTag_shouldThrowExceptionWithTagNotFoundCode_whenTagNotFound() {
        // given
        UUID tagId = UUID.randomUUID();
        TagCreateRequestDto dto = createTestTagCreateRequestDto();
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> tagService.updateTag(tagId, dto));

        // then
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.TAG_NOT_FOUND.getCode());
    }

    @Test
    void updateTag_shouldThrowExceptionWithTagAlreadyExistsCode_whenNewNameAlreadyUsed() {
        // given
        UUID tagId = testTag.getId();
        String newName = "ExistingTag";
        TagCreateRequestDto dto = new TagCreateRequestDto(newName, UUID.randomUUID());
        Tag existingTagWithSameName = createTestTag();
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));
        when(tagRepository.findByName(newName)).thenReturn(Optional.of(existingTagWithSameName));

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> tagService.updateTag(tagId, dto));

        // then
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.TAG_ALREADY_EXISTS.getCode());
    }

    @Test
    void updateTag_shouldCallRepositorySave_whenTagExistsAndNameNotChanged() {
        // given
        UUID tagId = testTag.getId();
        TagCreateRequestDto dto = new TagCreateRequestDto(TEST_TAG_NAME, UUID.randomUUID());
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));
        when(categoryService.getCategoryById(dto.categoryId())).thenReturn(createTestCategory());
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        // when
        tagService.updateTag(tagId, dto);

        // then
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    // --- deleteTag ---

    @Test
    void deleteTag_shouldCallRepositoryDelete_whenTagExists() {
        // given
        UUID tagId = testTag.getId();
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(testTag));

        // when
        tagService.deleteTag(tagId);

        // then
        verify(tagRepository, times(1)).delete(testTag);
    }

    @Test
    void deleteTag_shouldThrowExceptionWithTagNotFoundCode_whenTagNotFound() {
        // given
        UUID tagId = UUID.randomUUID();
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> tagService.deleteTag(tagId));

        // then
        assertThat(exception.getCode()).isEqualTo(BusinessExceptionReason.TAG_NOT_FOUND.getCode());
    }

    private Category createTestCategory() {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Test Category");
        return category;
    }
}
