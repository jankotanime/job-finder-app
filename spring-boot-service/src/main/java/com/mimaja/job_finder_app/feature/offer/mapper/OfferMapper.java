package com.mimaja.job_finder_app.feature.offer.mapper;

import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferSummaryResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUserIsOwnerResponseDto;
import com.mimaja.job_finder_app.feature.offer.location.mapper.LocationMapper;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.offer.tag.model.Tag;
import com.mimaja.job_finder_app.feature.user.mapper.UserMapper;
import com.mimaja.job_finder_app.feature.user.model.User;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {LocationMapper.class, UserMapper.class, TagMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OfferMapper {
    @Mappings({
        @Mapping(target = "owner", expression = "java(ownerFromRequest)"),
        @Mapping(target = "tags", expression = "java(tagsFromRequest)")
    })
    Offer toEntity(
            OfferCreateRequestDto offerCreateRequestDto,
            User ownerFromRequest,
            Set<Tag> tagsFromRequest);

    @Mapping(target = "tags", expression = "java(tagsFromRequest)")
    Offer toEntity(OfferUpdateRequestDto offerUpdateRequestDto, Set<Tag> tagsFromRequest);

    @Mapping(
            target = "candidatesAmount",
            expression = "java(offer.getCandidates() == null ? 0 : offer.getCandidates().size())")
    OfferSummaryResponseDto toOfferSummaryResponseDto(Offer offer);

    OfferUserIsOwnerResponseDto toOfferUserIsOwnerResponseDto(Offer offer);
}
