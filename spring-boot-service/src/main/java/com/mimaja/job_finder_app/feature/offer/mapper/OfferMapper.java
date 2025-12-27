package com.mimaja.job_finder_app.feature.offer.mapper;

import com.mimaja.job_finder_app.feature.application.mapper.ApplicationMapper;
import com.mimaja.job_finder_app.feature.cv.mapper.CvMapper;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferSummaryResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUserIsOwnerResponseDto;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, TagMapper.class, ApplicationMapper.class, CvMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OfferMapper {
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Offer toEntity(OfferCreateRequestDto offerCreateRequestDto);

    @Mapping(target = "tags", ignore = true)
    Offer toEntityFromUpdate(OfferUpdateRequestDto offerUpdateRequestDto);

    @Mapping(
            target = "candidatesAmount",
            expression =
                    "java(offer.getApplications() == null ? 0 : offer.getApplications().size())")
    OfferSummaryResponseDto toOfferSummaryResponseDto(Offer offer);

    OfferUserIsOwnerResponseDto toOfferUserIsOwnerResponseDto(Offer offer);
}
