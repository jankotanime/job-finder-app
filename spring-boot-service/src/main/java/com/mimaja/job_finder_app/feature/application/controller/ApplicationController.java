package com.mimaja.job_finder_app.feature.application.controller;

import com.mimaja.job_finder_app.feature.application.dto.ApplicationResponseDto;
import com.mimaja.job_finder_app.feature.application.service.ApplicationServiceUser;
import com.mimaja.job_finder_app.feature.offer.dto.OfferApplyRequestDto;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/offer/{offerId}/application")
public class ApplicationController {
    private final ApplicationServiceUser applicationServiceUser;
    private static final String ID = "/{applicationId}";

    @GetMapping
    public ResponseDto<Page<ApplicationResponseDto>> getAllApplicationsByOfferId(
            @PathVariable UUID offerId,
            @AuthenticationPrincipal JwtPrincipal jwt,
            @PageableDefault(size = 20, sort = "appliedAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched all applications for offer with id: " + offerId,
                applicationServiceUser.getAllApplicationsByOfferId(offerId, jwt, pageable));
    }

    @GetMapping(ID)
    public ResponseDto<ApplicationResponseDto> getApplicationById(
            @PathVariable UUID offerId,
            @PathVariable UUID applicationId,
            @AuthenticationPrincipal JwtPrincipal jwt) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched application with id: " + applicationId,
                applicationServiceUser.getApplicationById(offerId, applicationId, jwt));
    }

    @PostMapping
    public ResponseDto<ApplicationResponseDto> sendApplication(
            @PathVariable UUID offerId,
            @AuthenticationPrincipal JwtPrincipal jwt,
            @RequestBody @Valid OfferApplyRequestDto dto) {
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully sent application",
                applicationServiceUser.sendApplication(offerId, jwt, dto));
    }

    @PatchMapping(ID + "/accept")
    public ResponseDto<ApplicationResponseDto> acceptApplication(
            @PathVariable UUID offerId,
            @PathVariable UUID applicationId,
            @AuthenticationPrincipal JwtPrincipal jwt) {
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully accepted application",
                applicationServiceUser.acceptApplication(offerId, applicationId, jwt));
    }

    @PatchMapping(ID + "/reject")
    public ResponseDto<ApplicationResponseDto> rejectApplication(
            @PathVariable UUID offerId,
            @PathVariable UUID applicationId,
            @AuthenticationPrincipal JwtPrincipal jwt) {
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully rejected application",
                applicationServiceUser.rejectApplication(offerId, applicationId, jwt));
    }
}
