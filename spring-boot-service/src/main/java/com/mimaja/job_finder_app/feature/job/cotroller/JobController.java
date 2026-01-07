package com.mimaja.job_finder_app.feature.job.cotroller;

import com.mimaja.job_finder_app.feature.job.dto.JobResponseDto;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.dto.JobDispatcherResponseDto;
import com.mimaja.job_finder_app.feature.job.service.JobUserService;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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

    @GetMapping(ID + "/dispatcher")
    public ResponseDto<JobDispatcherResponseDto> getJobDispatcher(
            @AuthenticationPrincipal JwtPrincipal jwt, @PathVariable UUID jobId) {
        JobDispatcherResponseDto response = jobUserService.getJobDispatcher(jwt.id(), jobId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_CREATED,
                "Successfully started job with id: " + jobId,
                response);
    }

    @PostMapping(ID + "/start-job")
    public ResponseDto<JobDispatcherResponseDto> startJob(
            @AuthenticationPrincipal JwtPrincipal jwt, @PathVariable UUID jobId) {
        JobDispatcherResponseDto response = jobUserService.startJob(jwt.id(), jobId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_CREATED,
                "Successfully started job with id: " + jobId,
                response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = ID + "/finish-job")
    public ResponseDto<JobResponseDto> finishJob(
            @RequestParam("photo") Optional<MultipartFile> photo,
            @RequestParam("description") String description,
            @AuthenticationPrincipal JwtPrincipal jwt,
            @PathVariable UUID jobId) {
        JobResponseDto response =
                jobUserService.endJobSuccessfuly(jwt.id(), jobId, photo, description);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully started job with id: " + jobId,
                response);
    }

    @PatchMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            value = ID + "/report-problem-true")
    public ResponseDto<JobDispatcherResponseDto> reportProblemTrue(
            @RequestParam("photo") Optional<MultipartFile> photo,
            @RequestParam("description") String description,
            @AuthenticationPrincipal JwtPrincipal jwt,
            @PathVariable UUID jobId) {
        JobDispatcherResponseDto response =
                jobUserService.reportProblemTrue(jwt.id(), jobId, photo, description);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully reported there is problem for job with id: " + jobId,
                response);
    }

    @PatchMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            value = ID + "/report-problem-false")
    public ResponseDto<JobDispatcherResponseDto> reportProblemFalse(
            @RequestParam("photo") Optional<MultipartFile> photo,
            @RequestParam("description") String description,
            @AuthenticationPrincipal JwtPrincipal jwt,
            @PathVariable UUID jobId) {
        JobDispatcherResponseDto response =
                jobUserService.reportProblemFalse(jwt.id(), jobId, photo, description);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully reported there is no problem for job with id: " + jobId,
                response);
    }

    @DeleteMapping(ID)
    public ResponseDto<Void> deleteJob(
            @AuthenticationPrincipal JwtPrincipal jwt, @PathVariable UUID jobId) {
        jobUserService.deleteJob(jwt, jobId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED, "Successfully deleted job with id: " + jobId, null);
    }
}
