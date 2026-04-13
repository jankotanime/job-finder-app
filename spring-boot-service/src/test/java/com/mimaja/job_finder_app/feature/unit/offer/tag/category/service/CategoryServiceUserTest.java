package com.mimaja.job_finder_app.feature.unit.offer.tag.category.service;

import static com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata.CategoryMockData.createTestCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.mapper.CategoryMapper;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.CategoryColor;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryService;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryServiceUser;
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
class CategoryServiceUserTest {
    @Mock private CategoryService categoryService;
    @Mock private CategoryMapper categoryMapper;
    @InjectMocks private CategoryServiceUser categoryServiceUser;

    private Category testCategory;
    private CategoryResponseDto testResponseDto;
    private Pageable testPageable;

    @BeforeEach
    void setUp() {
        testCategory = createTestCategory();
        testResponseDto =
                new CategoryResponseDto(
                        testCategory.getId(), testCategory.getName(), CategoryColor.RED);
        testPageable = PageRequest.of(0, 10);
    }

    // --- getAllCategories ---

    @Test
    void getAllCategories_shouldReturnMappedPage_whenCategoriesExist() {
        // given
        Page<Category> categoryPage = new PageImpl<>(List.of(testCategory), testPageable, 1);
        when(categoryService.getAllCategories(any(), any())).thenReturn(categoryPage);
        when(categoryMapper.toResponseDto(testCategory)).thenReturn(testResponseDto);

        // when
        Page<CategoryResponseDto> result =
                categoryServiceUser.getAllCategories(
                        new CategoryFilterRequestDto(null), testPageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getAllCategories_shouldCallCategoryService_whenInvoked() {
        // given
        Page<Category> emptyPage = new PageImpl<>(List.of(), testPageable, 0);
        when(categoryService.getAllCategories(any(), any())).thenReturn(emptyPage);

        // when
        categoryServiceUser.getAllCategories(new CategoryFilterRequestDto(null), testPageable);

        // then
        verify(categoryService, times(1)).getAllCategories(any(), any());
    }

    // --- getCategoryById ---

    @Test
    void getCategoryById_shouldReturnNonNullResponseDto_whenCategoryExists() {
        // given
        UUID categoryId = testCategory.getId();
        when(categoryService.getCategoryById(categoryId)).thenReturn(testCategory);
        when(categoryMapper.toResponseDto(testCategory)).thenReturn(testResponseDto);

        // when
        CategoryResponseDto result = categoryServiceUser.getCategoryById(categoryId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void getCategoryById_shouldCallCategoryService_whenInvoked() {
        // given
        UUID categoryId = testCategory.getId();
        when(categoryService.getCategoryById(categoryId)).thenReturn(testCategory);
        when(categoryMapper.toResponseDto(any())).thenReturn(testResponseDto);

        // when
        categoryServiceUser.getCategoryById(categoryId);

        // then
        verify(categoryService, times(1)).getCategoryById(categoryId);
    }
}
