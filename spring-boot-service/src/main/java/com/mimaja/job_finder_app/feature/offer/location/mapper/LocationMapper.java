package com.mimaja.job_finder_app.feature.offer.location.mapper;

import com.mimaja.job_finder_app.feature.offer.location.dto.LocationCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.location.dto.LocationResponseDto;
import com.mimaja.job_finder_app.feature.offer.location.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {
    Location toEntity(LocationCreateRequestDto locationCreateRequestDto);

    LocationResponseDto toResponseDto(Location location);
}
