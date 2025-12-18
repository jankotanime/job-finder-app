package com.mimaja.job_finder_app.feature.cv.controller;

import com.mimaja.job_finder_app.feature.cv.dto.CvResponseDto;
import com.mimaja.job_finder_app.feature.cv.service.CvService;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cv")
public class CvController {
    private final CvService cvService;
    private static final String ID = "/{cvId}";
    private static final String USER_ID = "/{userId}";

    @PostMapping("/upload/{userId}")
    public ResponseEntity<ResponseDto<CvResponseDto>> uploadImage(
            @RequestParam("file") MultipartFile file, @PathVariable UUID userId) {
        CvResponseDto cv = cvService.uploadCv(file, userId);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(ID)
                        .buildAndExpand(cv.id())
                        .toUri();
        return ResponseEntity.created(location)
                .body(
                        new ResponseDto<>(
                                SuccessCode.RESOURCE_CREATED, "Successfully uploaded CV", cv));
    }

    @GetMapping(ID)
    public ResponseDto<CvResponseDto> getCvById(@PathVariable UUID cvId) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched CV with id: " + cvId,
                cvService.getCvById(cvId));
    }

    @GetMapping("/user" + USER_ID)
    public ResponseDto<List<CvResponseDto>> getCvsByUserId(@PathVariable UUID userId) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched Cvs for user with id: " + userId,
                cvService.getCvsByUserId(userId));
    }

    @PutMapping(ID)
    public ResponseDto<CvResponseDto> updateCv(MultipartFile file, @PathVariable UUID cvId) {
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully updated CV with id: " + cvId,
                cvService.updateCv(file, cvId));
    }

    @DeleteMapping(ID)
    public ResponseDto<Void> deleteCv(@PathVariable UUID cvId) {
        cvService.deleteCv(cvId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED, "Successfully deleted CV with id: " + cvId, null);
    }

    @DeleteMapping("/user" + USER_ID)
    public ResponseDto<Void> deleteCvsByUserId(@PathVariable UUID userId) {
        cvService.deleteAllCvsForUser(userId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED,
                "Successfully deleted Cvs for user with id: " + userId,
                null);
    }
}
