package com.mimaja.job_finder_app.feature.cv.controller;

import com.mimaja.job_finder_app.feature.cv.dto.CvResponseDto;
import com.mimaja.job_finder_app.feature.cv.service.CvUserService;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
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
    private final CvUserService cvUserService;
    private static final String ID = "/{cvId}";

    @PostMapping
    public ResponseEntity<ResponseDto<CvResponseDto>> uploadCv(
            @RequestParam("file") MultipartFile file, @AuthenticationPrincipal JwtPrincipal jwt) {
        CvResponseDto cv = cvUserService.uploadCv(file, jwt);
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
                cvUserService.getCvById(cvId));
    }

    @GetMapping
    public ResponseDto<List<CvResponseDto>> getCvsByUserId(
            @AuthenticationPrincipal JwtPrincipal jwt) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched Cvs for user with id: " + jwt.id(),
                cvUserService.getCvsByUserId(jwt.id()));
    }

    @PutMapping(ID)
    public ResponseDto<CvResponseDto> updateCv(
            MultipartFile file,
            @PathVariable UUID cvId,
            @AuthenticationPrincipal JwtPrincipal jwt) {
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully updated CV with id: " + cvId,
                cvUserService.updateCv(file, jwt, cvId));
    }

    @DeleteMapping(ID)
    public ResponseDto<Void> deleteCv(
            @PathVariable UUID cvId, @AuthenticationPrincipal JwtPrincipal jwt) {
        cvUserService.deleteCv(jwt, cvId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED, "Successfully deleted CV with id: " + cvId, null);
    }

    @DeleteMapping
    public ResponseDto<Void> deleteCvsByUserId(@AuthenticationPrincipal JwtPrincipal jwt) {
        cvUserService.deleteAllCvsForUser(jwt);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED,
                "Successfully deleted Cvs for user with id: " + jwt.id(),
                null);
    }
}
