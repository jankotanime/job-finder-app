package com.mimaja.job_finder_app.feature.offer.controller;

import com.mimaja.job_finder_app.feature.offer.dto.OfferApplyRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferBaseResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferSummaryResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUserIsOwnerResponseDto;
import com.mimaja.job_finder_app.feature.offer.service.OfferUserService;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/offer")
public class OfferController {
    private final OfferUserService offerUserService;
    private static final String ID = "/{offerId}";

    @GetMapping
    public ResponseDto<Page<OfferSummaryResponseDto>> getAllOffers(
            @RequestParam(required = false) LocalDateTime firstDate,
            @RequestParam(required = false) LocalDateTime lastDate,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) Set<UUID> categories,
            @RequestParam(required = false) Set<UUID> tags,
            @PageableDefault(sort = "createdAt", size = 20, direction = Sort.Direction.DESC)
                    Pageable pageable) {
        OfferFilterRequestDto offerFilterRequestDto =
                new OfferFilterRequestDto(
                        firstDate, lastDate, minSalary, maxSalary, categories, tags);
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched offers",
                offerUserService.getFilteredOffers(offerFilterRequestDto, pageable));
    }

    @GetMapping(ID)
    public ResponseDto<OfferBaseResponseDto> getOfferById(
            @PathVariable UUID offerId, @AuthenticationPrincipal JwtPrincipal jwt) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched offer with id: " + offerId,
                offerUserService.getOfferById(jwt, offerId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<OfferUserIsOwnerResponseDto>> createOffer(
            @AuthenticationPrincipal JwtPrincipal jwt,
            @RequestParam("photo") Optional<MultipartFile> photo,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("dateAndTime") LocalDateTime dateAndTime,
            @RequestParam("salary") Double salary,
            @RequestParam("maxApplications") int maxApplications,
            @RequestParam("tags") Set<UUID> tags) {
        OfferCreateRequestDto offerCreateRequestDto =
                new OfferCreateRequestDto(
                        title, description, dateAndTime, salary, maxApplications, tags);
        OfferUserIsOwnerResponseDto offerResponseDto =
                offerUserService.createOffer(photo, offerCreateRequestDto, jwt);

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(ID)
                        .buildAndExpand(offerResponseDto.id())
                        .toUri();

        return ResponseEntity.created(location)
                .body(
                        new ResponseDto<>(
                                SuccessCode.RESOURCE_CREATED,
                                "Successfully created offer",
                                offerResponseDto));
    }

    @PutMapping(value = ID, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<OfferUserIsOwnerResponseDto> updateOffer(
            @PathVariable UUID offerId,
            @AuthenticationPrincipal JwtPrincipal jwt,
            @RequestParam("photo") Optional<MultipartFile> photo,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("dateAndTime") LocalDateTime dateAndTime,
            @RequestParam("salary") Double salary,
            @RequestParam("maxApplications") int maxApplications,
            @RequestParam("tags") Set<UUID> tags) {
        OfferUpdateRequestDto offerUpdateRequestDto =
                new OfferUpdateRequestDto(
                        title, description, dateAndTime, salary, maxApplications, tags);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully updated offer with id: " + offerId,
                offerUserService.updateOffer(offerId, photo, offerUpdateRequestDto, jwt));
    }

    @DeleteMapping(ID)
    public ResponseDto<Void> deleteOffer(
            @PathVariable UUID offerId, @AuthenticationPrincipal JwtPrincipal jwt) {
        offerUserService.deleteOffer(offerId, jwt);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED,
                "Successfully deleted offer with id: " + offerId,
                null);
    }

    @PatchMapping(ID)
    public ResponseDto<OfferSummaryResponseDto> applyOffer(
            @PathVariable UUID offerId,
            @AuthenticationPrincipal JwtPrincipal jwt,
            @RequestBody @Valid OfferApplyRequestDto dto) {
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully applied for offer with id: " + offerId,
                offerUserService.applyOffer(offerId, jwt, dto));
    }
}
