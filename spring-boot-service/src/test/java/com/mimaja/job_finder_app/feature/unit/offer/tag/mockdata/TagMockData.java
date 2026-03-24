package com.mimaja.job_finder_app.feature.unit.offer.tag.mockdata;

import com.mimaja.job_finder_app.feature.offer.tag.category.model.Category;
import com.mimaja.job_finder_app.feature.offer.tag.category.model.CategoryColor;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TagMockData {
    public static final String TEST_TAG_NAME = "Test Tag";
    public static final String TEST_CATEGORY_NAME = "Test Category";
    public static final CategoryColor TEST_CATEGORY_COLOR = CategoryColor.BLUE;

    public static Tag createTestTag() {
        Tag tag = new Tag();
        tag.setId(UUID.randomUUID());
        tag.setName(TEST_TAG_NAME);
        return tag;
    }

    public static Tag createTestTagWithCategory() {
        Tag tag = createTestTag();
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(TEST_CATEGORY_NAME);
        category.setColor(TEST_CATEGORY_COLOR);
        tag.setCategory(category);
        return tag;
    }

    public static Tag createTestTagWithoutCategory() {
        Tag tag = createTestTag();
        tag.setCategory(null);
        return tag;
    }

    public static TagCreateRequestDto createTestTagCreateRequestDto() {
        return new TagCreateRequestDto(TEST_TAG_NAME, UUID.randomUUID());
    }

    public static Set<Tag> createTestTagSet() {
        Set<Tag> tags = new HashSet<>();
        tags.add(createTestTag());
        tags.add(createTestTag());
        return tags;
    }

    public static Set<Tag> createTestTagSetWithCategories() {
        Set<Tag> tags = new HashSet<>();
        tags.add(createTestTagWithCategory());
        tags.add(createTestTagWithCategory());
        return tags;
    }

    public static Category createTestCategory() {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(TEST_CATEGORY_NAME);
        category.setColor(TEST_CATEGORY_COLOR);
        return category;
    }
}
