package com.mimaja.job_finder_app.feature.job.dto;

import com.mimaja.job_finder_app.feature.job.jobphoto.dto.JobPhotoResponseDto;
import com.mimaja.job_finder_app.feature.job.model.JobStatus;
import com.mimaja.job_finder_app.feature.offer.tag.dto.TagResponseDto;
import com.mimaja.job_finder_app.feature.user.dto.UserInJobResponseDto;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record JobResponseDto(
        UUID id,
        String title,
        String description,
        LocalDateTime dateAndTime,
        Double salary,
        JobStatus status,
        UserInJobResponseDto owner,
        UserInJobResponseDto contractor,
        Set<TagResponseDto> tags,
        JobPhotoResponseDto photo) {}
