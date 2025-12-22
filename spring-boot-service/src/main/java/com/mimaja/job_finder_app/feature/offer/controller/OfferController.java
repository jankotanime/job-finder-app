package com.mimaja.job_finder_app.feature.offer.controller;

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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/offer")
public class OfferController {
    private final OfferUserService offerUserService;
    private static final String ID = "/{offerId}";

    @GetMapping
    public ResponseDto<Page<OfferSummaryResponseDto>> getAllOffers(
            @PageableDefault(sort = "createdAt", size = 20, direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched all offers",
                offerUserService.getAllOffers(pageable));
    }

    @GetMapping(ID)
    public ResponseDto<OfferBaseResponseDto> getOfferById(
            @PathVariable UUID offerId, @AuthenticationPrincipal JwtPrincipal jwt) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched offer with id: " + offerId,
                offerUserService.getOfferById(jwt, offerId));
    }

    @PostMapping
    public ResponseEntity<ResponseDto<OfferUserIsOwnerResponseDto>> createOffer(
            @Valid @RequestBody OfferCreateRequestDto offerCreateRequestDto,
            @AuthenticationPrincipal JwtPrincipal jwt) {
        OfferUserIsOwnerResponseDto offerResponseDto =
                offerUserService.createOffer(offerCreateRequestDto, jwt);

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

    @PutMapping(ID)
    public ResponseDto<OfferUserIsOwnerResponseDto> updateOffer(
            @PathVariable UUID offerId,
            @Valid @RequestBody OfferUpdateRequestDto offerUpdateRequestDto,
            @AuthenticationPrincipal JwtPrincipal jwt) {
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED,
                "Successfully updated offer with id: " + offerId,
                offerUserService.updateOffer(offerId, offerUpdateRequestDto, jwt));
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

    @GetMapping("/filter")
    public ResponseDto<Page<OfferSummaryResponseDto>> getFilteredOffers(
            @Valid @RequestBody OfferFilterRequestDto offerFilterRequestDto,
            @PageableDefault(sort = "createdAt", size = 20, direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched offers",
                offerUserService.getFilteredOffers(offerFilterRequestDto, pageable));
    }
}
