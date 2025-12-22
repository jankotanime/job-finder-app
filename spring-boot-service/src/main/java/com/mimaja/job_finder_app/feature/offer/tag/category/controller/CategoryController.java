package com.mimaja.job_finder_app.feature.offer.tag.category.controller;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.mapper.CategoryMapper;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryService;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    private static final String ID = "/{categoryId}";

    @GetMapping
    public ResponseDto<Page<CategoryResponseDto>> getAllCategories(
            @PageableDefault(sort = "name", size = 20, direction = Sort.Direction.ASC)
                    Pageable pageable) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched all categories",
                categoryService.getAllCategories(pageable).map(categoryMapper::toResponseDto));
    }

    @GetMapping(ID)
    public ResponseDto<CategoryResponseDto> getCategoryById(@PathVariable UUID categoryId) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched category with id: " + categoryId,
                categoryMapper.toResponseDto(categoryService.getCategoryById(categoryId)));
    }

    @PostMapping
    public ResponseEntity<ResponseDto<CategoryResponseDto>> getCategory(
            @RequestBody @Valid CategoryCreateRequestDto categoryCreateRequestDto) {
        CategoryResponseDto categoryResponseDto =
                categoryMapper.toResponseDto(
                        categoryService.createCategory(categoryCreateRequestDto));

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(ID)
                        .buildAndExpand(categoryResponseDto.id())
                        .toUri();

        return ResponseEntity.created(location)
                .body(
                        new ResponseDto<>(
                                SuccessCode.RESOURCE_CREATED,
                                "Successfully created category",
                                categoryResponseDto));
    }

    @PutMapping(ID)
    public ResponseDto<CategoryResponseDto> updateCategory(
            @PathVariable UUID categoryId,
            @RequestBody @Valid CategoryCreateRequestDto categoryUpdateRequestDto) {
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully updated category with id: " + categoryId,
                categoryMapper.toResponseDto(
                        categoryService.updateCategory(categoryId, categoryUpdateRequestDto)));
    }

    @DeleteMapping(ID)
    public ResponseDto<Void> deleteCategory(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED,
                "Successfully deleted category with id: " + categoryId,
                null);
    }
}
