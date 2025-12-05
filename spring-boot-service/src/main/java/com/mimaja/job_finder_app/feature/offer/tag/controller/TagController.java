package com.mimaja.job_finder_app.feature.offer.tag.controller;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagService;
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
@RequestMapping("/tag")
public class TagController {
    private final TagService tagService;
    private static final String ID = "/{tagId}";

    @PostMapping
    public ResponseEntity<ResponseDto<TagResponseDto>> getCategory(
            @RequestBody TagCreateRequestDto tagCreateRequestDto) {
        TagResponseDto tagResponseDto = tagService.createTag(tagCreateRequestDto);

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
}
