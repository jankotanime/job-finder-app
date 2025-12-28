package com.mimaja.job_finder_app.feature.application.mapper;

import com.mimaja.job_finder_app.feature.application.dto.ApplicationCreateRequestDto;
import com.mimaja.job_finder_app.feature.application.dto.ApplicationResponseDto;
import com.mimaja.job_finder_app.feature.application.model.Application;
import com.mimaja.job_finder_app.feature.cv.mapper.CvMapper;
import com.mimaja.job_finder_app.feature.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, CvMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApplicationMapper {
    Application toEntity(ApplicationCreateRequestDto dto);

    ApplicationResponseDto toResponseDto(Application application);
}
