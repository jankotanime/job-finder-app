package com.mimaja.job_finder_app.feature.unit.offer.tag.category.service;

import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.TEST_CATEGORY_NAME;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.createTestCategory;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.createTestCategoryCreateRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.mapper.CategoryMapper;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.CategoryColor;
import com.mimaja.job_finder_app.feature.offer.tag.category.repository.CategoryRepository;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryServiceDefault;
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
class CategoryServiceDefaultTest {
    @Mock private CategoryRepository categoryRepository;
    @Mock private CategoryMapper categoryMapper;
    @InjectMocks private CategoryServiceDefault categoryService;

    private Category testCategory;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testCategory = createTestCategory();
        testPageable = PageRequest.of(0, 10);
    }

    // --- getAllCategories ---

    @Test
    @SuppressWarnings("unchecked")
    void getAllCategories_shouldReturnPage_whenCategoriesExist() {
        // given
        Page<Category> expectedPage = new PageImpl<>(List.of(testCategory), testPageable, 1);
        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        // when
        Page<Category> result =
                categoryService.getAllCategories(new CategoryFilterRequestDto(null), testPageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAllCategories_shouldCallRepository_whenInvoked() {
        // given
        Page<Category> emptyPage = new PageImpl<>(List.of(), testPageable, 0);
        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        // when
        categoryService.getAllCategories(new CategoryFilterRequestDto(null), testPageable);

        // then
        verify(categoryRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    // --- getCategoryById ---

    @Test
    void getCategoryById_shouldReturnCategory_whenCategoryExists() {
        // given
        UUID id = testCategory.getId();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(testCategory));

        // when
        Category result = categoryService.getCategoryById(id);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void getCategoryById_shouldReturnCorrectCategory_whenCategoryExists() {
        // given
        UUID id = testCategory.getId();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(testCategory));

        // when
        Category result = categoryService.getCategoryById(id);

        // then
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void getCategoryById_shouldThrowExceptionWithCategoryNotFoundCode_whenCategoryNotFound() {
        // given
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> categoryService.getCategoryById(id));

        // then
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.CATEGORY_NOT_FOUND.getCode());
    }

    // --- createCategory ---

    @Test
    void createCategory_shouldReturnSavedCategory_whenCategoryNameNotExists() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();
        when(categoryRepository.findByName(dto.name())).thenReturn(Optional.empty());
        when(categoryMapper.toEntity(dto)).thenReturn(testCategory);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // when
        Category result = categoryService.createCategory(dto);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void createCategory_shouldCallRepositorySave_whenCategoryNameNotExists() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();
        when(categoryRepository.findByName(dto.name())).thenReturn(Optional.empty());
        when(categoryMapper.toEntity(dto)).thenReturn(testCategory);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // when
        categoryService.createCategory(dto);

        // then
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_shouldThrowExceptionWithCategoryAlreadyExistsCode_whenNameExists() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();
        when(categoryRepository.findByName(dto.name())).thenReturn(Optional.of(testCategory));

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> categoryService.createCategory(dto));

        // then
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.CATEGORY_ALREADY_EXISTS.getCode());
    }

    @Test
    void createCategory_shouldNotCallRepositorySave_whenCategoryNameExists() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();
        when(categoryRepository.findByName(dto.name())).thenReturn(Optional.of(testCategory));

        // when
        assertThrows(BusinessException.class, () -> categoryService.createCategory(dto));

        // then
        verify(categoryRepository, times(0)).save(any(Category.class));
    }

    // --- updateCategory ---

    @Test
    void updateCategory_shouldReturnUpdatedCategory_whenCategoryExistsAndNameNotChanged() {
        // given
        UUID id = testCategory.getId();
        CategoryCreateRequestDto dto =
                new CategoryCreateRequestDto(TEST_CATEGORY_NAME, CategoryColor.RED);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // when
        Category result = categoryService.updateCategory(id, dto);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void updateCategory_shouldThrowExceptionWithCategoryNotFoundCode_whenCategoryNotFound() {
        // given
        UUID id = UUID.randomUUID();
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // when
        BusinessException exception =
                assertThrows(
                        BusinessException.class, () -> categoryService.updateCategory(id, dto));

        // then
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.CATEGORY_NOT_FOUND.getCode());
    }

    @Test
    void updateCategory_shouldThrowExceptionWithCategoryAlreadyExistsCode_whenNewNameAlreadyUsed() {
        // given
        UUID id = testCategory.getId();
        String newName = "ExistingCategory";
        CategoryCreateRequestDto dto = new CategoryCreateRequestDto(newName, CategoryColor.BLUE);
        Category existingCategoryWithSameName = createTestCategory();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.findByName(newName))
                .thenReturn(Optional.of(existingCategoryWithSameName));

        // when
        BusinessException exception =
                assertThrows(
                        BusinessException.class, () -> categoryService.updateCategory(id, dto));

        // then
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.CATEGORY_ALREADY_EXISTS.getCode());
    }

    @Test
    void updateCategory_shouldCallRepositorySave_whenCategoryExistsAndNameNotChanged() {
        // given
        UUID id = testCategory.getId();
        CategoryCreateRequestDto dto =
                new CategoryCreateRequestDto(TEST_CATEGORY_NAME, CategoryColor.RED);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // when
        categoryService.updateCategory(id, dto);

        // then
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    // --- deleteCategory ---

    @Test
    void deleteCategory_shouldCallRepositoryDelete_whenCategoryExists() {
        // given
        UUID id = testCategory.getId();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(testCategory));

        // when
        categoryService.deleteCategory(id);

        // then
        verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test
    void deleteCategory_shouldThrowExceptionWithCategoryNotFoundCode_whenCategoryNotFound() {
        // given
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> categoryService.deleteCategory(id));

        // then
        assertThat(exception.getCode())
                .isEqualTo(BusinessExceptionReason.CATEGORY_NOT_FOUND.getCode());
    }
}
