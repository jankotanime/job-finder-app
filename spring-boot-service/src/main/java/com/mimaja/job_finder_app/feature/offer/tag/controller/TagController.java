package com.mimaja.job_finder_app.feature.offer.tag.controller;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagService;
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
@RequestMapping("/tag")
public class TagController {
    private final TagService tagService;
    private final TagMapper tagMapper;
    private static final String ID = "/{tagId}";

    @GetMapping
    public ResponseDto<Page<TagResponseDto>> getAllTags(
            @PageableDefault(sort = "name", size = 20, direction = Sort.Direction.ASC)
                    Pageable pageable) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched all tags",
                tagService.getAllTags(pageable).map(tagMapper::toResponseDto));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseDto<Page<TagResponseDto>> getAllByCategoryId(
            @PathVariable UUID categoryId,
            @PageableDefault(sort = "name", size = 20, direction = Sort.Direction.ASC)
                    Pageable pageable) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched all tags for category with id: " + categoryId,
                tagService.getAllByCategoryId(categoryId, pageable).map(tagMapper::toResponseDto));
    }

    @GetMapping(ID)
    public ResponseDto<TagResponseDto> getTagById(@PathVariable UUID tagId) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched tag with id: " + tagId,
                tagMapper.toResponseDto(tagService.getTagById(tagId)));
    }
}
