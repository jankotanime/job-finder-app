package com.mimaja.job_finder_app.feature.offer.tag.category.controller;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryService;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    private static final String ID = "/{categoryId}";

    @PostMapping
    public ResponseEntity<ResponseDto<CategoryResponseDto>> getCategory(
            @RequestBody CategoryCreateRequestDto categoryCreateRequestDto) {
        CategoryResponseDto categoryResponseDto =
                categoryService.createCategory(categoryCreateRequestDto);

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
}
