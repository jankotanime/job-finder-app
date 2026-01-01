package com.mimaja.job_finder_app.feature.offer.tag.controller;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagServiceAdmin;
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
@RequestMapping("/admin/tag")
public class TagControllerAdmin {
    private final TagServiceAdmin tagServiceAdmin;
    private static final String ID = "/{tagId}";

    @PostMapping
    public ResponseEntity<ResponseDto<TagResponseDto>> createTag(
            @RequestBody @Valid TagCreateRequestDto tagCreateRequestDto) {
        TagResponseDto tagResponseDto = tagServiceAdmin.createTag(tagCreateRequestDto);

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
                tagServiceAdmin.updateTag(tagId, tagUpdateRequestDto));
    }

    @DeleteMapping(ID)
    public ResponseDto<Void> deleteTag(@PathVariable UUID tagId) {
        tagServiceAdmin.deleteTag(tagId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED, "Successfully deleted tag with id: " + tagId, null);
    }
}
