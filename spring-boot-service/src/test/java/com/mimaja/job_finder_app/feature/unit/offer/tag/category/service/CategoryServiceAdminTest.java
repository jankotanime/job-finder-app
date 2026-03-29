package com.mimaja.job_finder_app.feature.unit.offer.tag.category.service;

import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.createTestCategory;
import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.createTestCategoryCreateRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.mapper.CategoryMapper;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.CategoryColor;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryService;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryServiceAdmin;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceAdminTest {
    @Mock private CategoryService categoryService;
    @Mock private CategoryMapper categoryMapper;
    @InjectMocks private CategoryServiceAdmin categoryServiceAdmin;

    private Category testCategory;
    private CategoryResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        testCategory = createTestCategory();
        testResponseDto = createTestCategoryResponseDto();
    }

    // --- createCategory ---

    @Test
    void createCategory_shouldReturnNonNullResponseDto_whenCategoryCreated() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();
        when(categoryService.createCategory(dto)).thenReturn(testCategory);
        when(categoryMapper.toResponseDto(testCategory)).thenReturn(testResponseDto);

        // when
        CategoryResponseDto result = categoryServiceAdmin.createCategory(dto);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void createCategory_shouldCallCategoryService_whenInvoked() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();
        when(categoryService.createCategory(dto)).thenReturn(testCategory);
        when(categoryMapper.toResponseDto(any())).thenReturn(testResponseDto);

        // when
        categoryServiceAdmin.createCategory(dto);

        // then
        verify(categoryService, times(1)).createCategory(dto);
    }

    @Test
    void createCategory_shouldCallMapper_whenInvoked() {
        // given
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();
        when(categoryService.createCategory(dto)).thenReturn(testCategory);
        when(categoryMapper.toResponseDto(testCategory)).thenReturn(testResponseDto);

        // when
        categoryServiceAdmin.createCategory(dto);

        // then
        verify(categoryMapper, times(1)).toResponseDto(testCategory);
    }

    // --- updateCategory ---

    @Test
    void updateCategory_shouldReturnNonNullResponseDto_whenCategoryUpdated() {
        // given
        UUID categoryId = testCategory.getId();
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();
        when(categoryService.updateCategory(categoryId, dto)).thenReturn(testCategory);
        when(categoryMapper.toResponseDto(testCategory)).thenReturn(testResponseDto);

        // when
        CategoryResponseDto result = categoryServiceAdmin.updateCategory(categoryId, dto);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void updateCategory_shouldCallCategoryService_whenInvoked() {
        // given
        UUID categoryId = testCategory.getId();
        CategoryCreateRequestDto dto = createTestCategoryCreateRequestDto();
        when(categoryService.updateCategory(categoryId, dto)).thenReturn(testCategory);
        when(categoryMapper.toResponseDto(any())).thenReturn(testResponseDto);

        // when
        categoryServiceAdmin.updateCategory(categoryId, dto);

        // then
        verify(categoryService, times(1)).updateCategory(categoryId, dto);
    }

    // --- deleteCategory ---

    @Test
    void deleteCategory_shouldCallCategoryService_whenInvoked() {
        // given
        UUID categoryId = testCategory.getId();

        // when
        categoryServiceAdmin.deleteCategory(categoryId);

        // then
        verify(categoryService, times(1)).deleteCategory(categoryId);
    }

    private CategoryResponseDto createTestCategoryResponseDto() {
        return new CategoryResponseDto(
                testCategory.getId(), testCategory.getName(), CategoryColor.RED);
    }
}
