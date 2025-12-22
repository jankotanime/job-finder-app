package com.mimaja.job_finder_app.feature.offer.tag.controller;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagService;
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

    @PostMapping
    public ResponseEntity<ResponseDto<TagResponseDto>> getCategory(
            @RequestBody @Valid TagCreateRequestDto tagCreateRequestDto) {
        TagResponseDto tagResponseDto =
                tagMapper.toResponseDto(tagService.createTag(tagCreateRequestDto));

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(ID)
                        .buildAndExpand(tagResponseDto.id())
                        .toUri();

        return ResponseEntity.created(location)
                .body(
                        new ResponseDto<>(
                                SuccessCode.RESOURCE_CREATED,
                                "Successfully created tag",
                                tagResponseDto));
    }

    @PutMapping(ID)
    public ResponseDto<TagResponseDto> updateTag(
            @PathVariable UUID tagId, @RequestBody @Valid TagCreateRequestDto tagUpdateRequestDto) {
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully updated tag with id: " + tagId,
                tagMapper.toResponseDto(tagService.updateTag(tagId, tagUpdateRequestDto)));
    }

    @DeleteMapping(ID)
    public ResponseDto<Void> deleteTag(@PathVariable UUID tagId) {
        tagService.deleteTag(tagId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED, "Successfully deleted tag with id: " + tagId, null);
    }
}
