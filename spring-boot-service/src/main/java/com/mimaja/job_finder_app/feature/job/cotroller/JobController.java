package com.mimaja.job_finder_app.feature.job.cotroller;

import com.mimaja.job_finder_app.feature.job.dto.JobResponseDto;
import com.mimaja.job_finder_app.feature.job.service.JobUserService;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/job")
public class JobController {
    private final JobUserService jobUserService;
    private static final String ID = "/{jobId}";

    @GetMapping(ID)
    public ResponseDto<JobResponseDto> getJobById(
            @AuthenticationPrincipal JwtPrincipal jwt, @PathVariable UUID jobId) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched job with id: " + jobId,
                jobUserService.getJobById(jwt, jobId));
    }

    @GetMapping("/contractor")
    public ResponseDto<List<JobResponseDto>> getJobsAsContractor(
            @AuthenticationPrincipal JwtPrincipal jwt) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched all jobs in which user is contractor",
                jobUserService.getJobsAsContractor(jwt));
    }

    @GetMapping("/owner")
    public ResponseDto<List<JobResponseDto>> getJobsAsOwner(
            @AuthenticationPrincipal JwtPrincipal jwt) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched all jobs in which user is owner",
                jobUserService.getJobsAsOwner(jwt));
    }

    @PostMapping("/{offerId}")
    public ResponseEntity<ResponseDto<JobResponseDto>> createJob(
            @AuthenticationPrincipal JwtPrincipal jwt, @PathVariable UUID offerId) {
        JobResponseDto jobResponseDto = jobUserService.createJob(jwt, offerId);

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(ID)
                        .buildAndExpand(jobResponseDto.id())
                        .toUri();

        return ResponseEntity.created(location)
                .body(
                        new ResponseDto<>(
                                SuccessCode.RESOURCE_CREATED,
                                "Successfully created job",
                                jobResponseDto));
    }

    @DeleteMapping(ID)
    public ResponseDto<Void> deleteJob(
            @AuthenticationPrincipal JwtPrincipal jwt, @PathVariable UUID jobId) {
        jobUserService.deleteJob(jwt, jobId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED, "Successfully deleted job with id: " + jobId, null);
    }
}
