package com.mimaja.job_finder_app.feature.offer.tag.category.controller;

import com.mimaja.job_finder_app.feature.offer.tag.category.dto.CategoryResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.category.service.CategoryServiceUser;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryServiceUser categoryServiceUser;
    private static final String ID = "/{categoryId}";

    @GetMapping
    public ResponseDto<Page<CategoryResponseDto>> getAllCategories(
            @PageableDefault(sort = "name", size = 20, direction = Sort.Direction.ASC)
                    Pageable pageable) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched all categories",
                categoryServiceUser.getAllCategories(pageable));
    }

    @GetMapping(ID)
    public ResponseDto<CategoryResponseDto> getCategoryById(@PathVariable UUID categoryId) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched category with id: " + categoryId,
                categoryServiceUser.getCategoryById(categoryId));
    }
}
