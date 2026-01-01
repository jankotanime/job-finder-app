package com.mimaja.job_finder_app.feature.offer.tag.category.controller;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryServiceAdmin;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/category")
public class CategoryControllerAdmin {
    private final CategoryServiceAdmin categoryServiceAdmin;
    private static final String ID = "/{categoryId}";

    @PostMapping
    public ResponseEntity<ResponseDto<CategoryResponseDto>> createCategory(
            @RequestBody @Valid CategoryCreateRequestDto categoryCreateRequestDto) {
        CategoryResponseDto categoryResponseDto =
                categoryServiceAdmin.createCategory(categoryCreateRequestDto);

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
                categoryServiceAdmin.updateCategory(categoryId, categoryUpdateRequestDto));
    }

    @DeleteMapping(ID)
    public ResponseDto<Void> deleteCategory(@PathVariable UUID categoryId) {
        categoryServiceAdmin.deleteCategory(categoryId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED,
                "Successfully deleted category with id: " + categoryId,
                null);
    }
}
