package com.mimaja.job_finder_app.feature.unit.offer.tag.category.mockdata;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.CategoryColor;
import java.util.UUID;

public class CategoryMockData {
    public static final String TEST_CATEGORY_NAME = "Test Category";
    public static final CategoryColor TEST_CATEGORY_COLOR = CategoryColor.RED;

    public static Category createTestCategory() {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(TEST_CATEGORY_NAME);
        category.setColor(TEST_CATEGORY_COLOR);
        return category;
    }

    public static Category createTestCategoryWithDifferentColor() {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(TEST_CATEGORY_NAME);
        category.setColor(CategoryColor.BLUE);
        return category;
    }

    public static CategoryCreateRequestDto createTestCategoryCreateRequestDto() {
        return new CategoryCreateRequestDto(TEST_CATEGORY_NAME, TEST_CATEGORY_COLOR);
    }

    public static CategoryCreateRequestDto createTestCategoryCreateRequestDtoWithDifferentColor() {
        return new CategoryCreateRequestDto(TEST_CATEGORY_NAME, CategoryColor.GREEN);
    }
}
