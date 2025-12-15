package com.mimaja.job_finder_app.feature.cv.mapper;

import com.mimaja.job_finder_app.feature.cv.dto.CvUploadRequestDto;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CvMapper {
    @Mapping(target = "user", ignore = true)
    Cv toEntity(CvUploadRequestDto dto);
}
