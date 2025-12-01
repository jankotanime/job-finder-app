package com.mimaja.job_finder_app.feature.offer.tag.mapper;

import com.mimaja.job_finder_app.feature.offer.tag.dto.TagCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {
    Tag toEntity(TagCreateRequestDto tagCreateRequestDto);

    @Mappings({
        @Mapping(source = "category.name", target = "categoryName"),
        @Mapping(source = "category.color", target = "categoryColor")
    })
    TagResponseDto toResponseDto(Tag tag);

    Set<TagResponseDto> toSetOfResponseDto(Set<Tag> tags);
}
