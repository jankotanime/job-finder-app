package com.mimaja.job_finder_app.feature.unit.offer.service;

import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestOfferWithOwner;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.offer.dto.OfferBaseResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferSummaryResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUserIsOwnerResponseDto;
import com.mimaja.job_finder_app.feature.offer.mapper.OfferMapper;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.offer.service.OfferUserService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("OfferUserService - Unit Tests")
public class OfferUserServiceTest {
    @Mock private OfferService offerService;

    @Mock private OfferMapper offerMapper;

    private OfferUserService offerUserService;

    private Offer testOffer;
    private User testOwner;
    private User testDifferentUser;
    private JwtPrincipal ownerJwt;
    private JwtPrincipal differentUserJwt;

    @BeforeEach
    void setUp() {
        offerUserService = new OfferUserService(offerService, offerMapper);

        testOwner = createTestUser();
        testDifferentUser = createTestUser();
        testOffer = createTestOfferWithOwner(testOwner);

        ownerJwt = createJwtPrincipal(testOwner);
        differentUserJwt = createJwtPrincipal(testDifferentUser);
    }

    // ==================== Get Offer By Id Tests ====================

    @Test
    @DisplayName("Should return offer user is owner response when user is owner")
    void testGetOfferById_WithUserIsOwner_ShouldReturnOfferUserIsOwnerResponseDto() {
        // given
        UUID offerId = testOffer.getId();
        OfferUserIsOwnerResponseDto expectedDto =
                org.mockito.Mockito.mock(OfferUserIsOwnerResponseDto.class);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(offerMapper.toOfferUserIsOwnerResponseDto(testOffer)).thenReturn(expectedDto);

        // when
        OfferBaseResponseDto result = offerUserService.getOfferById(ownerJwt, offerId);

        // then
        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Should return offer summary response when user is not owner")
    void testGetOfferById_WithUserIsNotOwner_ShouldReturnOfferSummaryResponseDto() {
        // given
        UUID offerId = testOffer.getId();
        OfferSummaryResponseDto expectedDto =
                org.mockito.Mockito.mock(OfferSummaryResponseDto.class);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(offerMapper.toOfferSummaryResponseDto(testOffer)).thenReturn(expectedDto);

        // when
        OfferBaseResponseDto result = offerUserService.getOfferById(differentUserJwt, offerId);

        // then
        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Should call offerService getOfferById before checking ownership")
    void testGetOfferById_ShouldCallOfferServiceGetOfferById() {
        // given
        UUID offerId = testOffer.getId();
        OfferUserIsOwnerResponseDto expectedDto =
                org.mockito.Mockito.mock(OfferUserIsOwnerResponseDto.class);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(offerMapper.toOfferUserIsOwnerResponseDto(testOffer)).thenReturn(expectedDto);

        // when
        offerUserService.getOfferById(ownerJwt, offerId);

        // then
        verify(offerService, times(1)).getOfferById(offerId);
    }

    @Test
    @DisplayName("Should call offerService getOfferById twice when user is not owner")
    void testGetOfferById_WithUserIsNotOwner_ShouldCallOfferServiceGetOfferByIdTwice() {
        // given
        UUID offerId = testOffer.getId();
        OfferSummaryResponseDto expectedDto =
                org.mockito.Mockito.mock(OfferSummaryResponseDto.class);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(offerMapper.toOfferSummaryResponseDto(testOffer)).thenReturn(expectedDto);

        // when
        offerUserService.getOfferById(differentUserJwt, offerId);

        // then
        verify(offerService, times(2)).getOfferById(offerId);
    }

    // ==================== Create Offer Tests ====================

    @Test
    @DisplayName("Should create offer and return user is owner response")
    void testCreateOffer_WithValidData_ShouldReturnOfferUserIsOwnerResponseDto() {
        // given
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();
        OfferUserIsOwnerResponseDto expectedDto =
                org.mockito.Mockito.mock(OfferUserIsOwnerResponseDto.class);

        when(offerService.createOffer(
                        ArgumentMatchers.<Optional<MultipartFile>>any(),
                        any(OfferCreateRequestDto.class),
                        any(UUID.class)))
                .thenReturn(testOffer);
        when(offerMapper.toOfferUserIsOwnerResponseDto(testOffer)).thenReturn(expectedDto);

        // when
        OfferUserIsOwnerResponseDto result =
                offerUserService.createOffer(Optional.empty(), requestDto, ownerJwt);

        // then
        assertNotNull(result, "Created offer response should not be null");
    }

    @Test
    @DisplayName("Should call offerService createOffer with correct parameters")
    void testCreateOffer_ShouldCallOfferServiceCreateOfferWithCorrectParameters() {
        // given
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();
        MultipartFile photo = org.mockito.Mockito.mock(MultipartFile.class);
        Optional<MultipartFile> photoOptional = Optional.of(photo);

        when(offerService.createOffer(photoOptional, requestDto, ownerJwt.id()))
                .thenReturn(testOffer);
        when(offerMapper.toOfferUserIsOwnerResponseDto(testOffer))
                .thenReturn(org.mockito.Mockito.mock(OfferUserIsOwnerResponseDto.class));

        // when
        offerUserService.createOffer(photoOptional, requestDto, ownerJwt);

        // then
        verify(offerService, times(1)).createOffer(photoOptional, requestDto, ownerJwt.id());
    }

    @Test
    @DisplayName("Should map created offer to response dto")
    void testCreateOffer_ShouldMapOfferToResponseDto() {
        // given
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();
        OfferUserIsOwnerResponseDto expectedDto =
                org.mockito.Mockito.mock(OfferUserIsOwnerResponseDto.class);

        when(offerService.createOffer(
                        ArgumentMatchers.<Optional<MultipartFile>>any(),
                        any(OfferCreateRequestDto.class),
                        any(UUID.class)))
                .thenReturn(testOffer);
        when(offerMapper.toOfferUserIsOwnerResponseDto(testOffer)).thenReturn(expectedDto);

        // when
        offerUserService.createOffer(Optional.empty(), requestDto, ownerJwt);

        // then
        verify(offerMapper, times(1)).toOfferUserIsOwnerResponseDto(testOffer);
    }

    // ==================== Update Offer Tests ====================

    @Test
    @DisplayName("Should update offer when user is owner")
    void testUpdateOffer_WithUserIsOwner_ShouldReturnUpdatedOffer() {
        // given
        UUID offerId = testOffer.getId();
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();
        OfferUserIsOwnerResponseDto expectedDto =
                org.mockito.Mockito.mock(OfferUserIsOwnerResponseDto.class);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(offerService.updateOffer(
                        any(UUID.class),
                        ArgumentMatchers.<Optional<MultipartFile>>any(),
                        any(OfferUpdateRequestDto.class)))
                .thenReturn(testOffer);
        when(offerMapper.toOfferUserIsOwnerResponseDto(testOffer)).thenReturn(expectedDto);

        // when
        OfferUserIsOwnerResponseDto result =
                offerUserService.updateOffer(offerId, Optional.empty(), requestDto, ownerJwt);

        // then
        assertNotNull(result, "Updated offer should not be null");
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not owner during update")
    void testUpdateOffer_WithUserIsNotOwner_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () ->
                                offerUserService.updateOffer(
                                        offerId, Optional.empty(), requestDto, differentUserJwt),
                        "Should throw BusinessException when user is not owner");

        assertThat(exception.getCode())
                .as("Exception code should indicate user is not owner")
                .isEqualTo(BusinessExceptionReason.USER_NOT_OWNER.getCode());
    }

    @Test
    @DisplayName("Should verify ownership before updating offer")
    void testUpdateOffer_ShouldVerifyOwnershipBeforeUpdating() {
        // given
        UUID offerId = testOffer.getId();
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();
        OfferUserIsOwnerResponseDto expectedDto =
                org.mockito.Mockito.mock(OfferUserIsOwnerResponseDto.class);

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);
        when(offerService.updateOffer(
                        any(UUID.class),
                        ArgumentMatchers.<Optional<MultipartFile>>any(),
                        any(OfferUpdateRequestDto.class)))
                .thenReturn(testOffer);
        when(offerMapper.toOfferUserIsOwnerResponseDto(testOffer)).thenReturn(expectedDto);

        // when
        offerUserService.updateOffer(offerId, Optional.empty(), requestDto, ownerJwt);

        // then
        verify(offerService, times(1)).getOfferById(offerId);
    }

    // ==================== Delete Offer Tests ====================

    @Test
    @DisplayName("Should delete offer when user is owner")
    void testDeleteOffer_WithUserIsOwner_ShouldCallOfferServiceDeleteOffer() {
        // given
        UUID offerId = testOffer.getId();

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        // when
        offerUserService.deleteOffer(offerId, ownerJwt);

        // then
        verify(offerService, times(1)).deleteOffer(offerId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not owner during delete")
    void testDeleteOffer_WithUserIsNotOwner_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> offerUserService.deleteOffer(offerId, differentUserJwt),
                        "Should throw BusinessException when user is not owner");

        assertThat(exception.getCode())
                .as("Exception code should indicate user is not owner")
                .isEqualTo(BusinessExceptionReason.USER_NOT_OWNER.getCode());
    }

    @Test
    @DisplayName("Should verify ownership before deleting offer")
    void testDeleteOffer_ShouldVerifyOwnershipBeforeDeleting() {
        // given
        UUID offerId = testOffer.getId();

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        // when
        offerUserService.deleteOffer(offerId, ownerJwt);

        // then
        verify(offerService, times(1)).getOfferById(offerId);
    }

    @Test
    @DisplayName("Should not delete offer when ownership verification fails")
    void testDeleteOffer_WithUserIsNotOwner_ShouldNotCallDeleteOffer() {
        // given
        UUID offerId = testOffer.getId();

        when(offerService.getOfferById(offerId)).thenReturn(testOffer);

        // when & then
        assertThrows(
                BusinessException.class,
                () -> offerUserService.deleteOffer(offerId, differentUserJwt));

        verify(offerService, times(0)).deleteOffer(offerId);
    }

    // ==================== Get Filtered Offers Tests ====================

    @Test
    @DisplayName("Should return filtered offers page")
    void testGetFilteredOffers_WithValidFilter_ShouldReturnPageOfOffers() {
        // given
        OfferFilterRequestDto filterDto = createTestOfferFilterRequestDto();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Offer> offersPage = new PageImpl<>(java.util.List.of(testOffer), pageable, 1);
        OfferSummaryResponseDto summaryDto =
                org.mockito.Mockito.mock(OfferSummaryResponseDto.class);

        when(offerService.getFilteredOffers(filterDto, pageable)).thenReturn(offersPage);
        when(offerMapper.toOfferSummaryResponseDto(testOffer)).thenReturn(summaryDto);

        // when
        Page<OfferSummaryResponseDto> result =
                offerUserService.getFilteredOffers(filterDto, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).contains(summaryDto);
    }

    @Test
    @DisplayName("Should return empty page when no offers match filter")
    void testGetFilteredOffers_WithNoMatches_ShouldReturnEmptyPage() {
        // given
        OfferFilterRequestDto filterDto = createTestOfferFilterRequestDto();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Offer> emptyPage = new PageImpl<>(java.util.List.of(), pageable, 0);

        when(offerService.getFilteredOffers(filterDto, pageable)).thenReturn(emptyPage);

        // when
        Page<OfferSummaryResponseDto> result =
                offerUserService.getFilteredOffers(filterDto, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Should call offerService getFilteredOffers")
    void testGetFilteredOffers_ShouldCallOfferServiceGetFilteredOffers() {
        // given
        OfferFilterRequestDto filterDto = createTestOfferFilterRequestDto();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Offer> offersPage = new PageImpl<>(java.util.List.of(), pageable, 0);

        when(offerService.getFilteredOffers(filterDto, pageable)).thenReturn(offersPage);

        // when
        offerUserService.getFilteredOffers(filterDto, pageable);

        // then
        verify(offerService, times(1)).getFilteredOffers(filterDto, pageable);
    }

    @Test
    @DisplayName("Should map all offers to summary response dtos")
    void testGetFilteredOffers_ShouldMapAllOffersToSummaryDtos() {
        // given
        OfferFilterRequestDto filterDto = createTestOfferFilterRequestDto();
        Pageable pageable = PageRequest.of(0, 10);
        Offer secondOffer = createTestOfferWithOwner(testOwner);
        Page<Offer> offersPage =
                new PageImpl<>(java.util.List.of(testOffer, secondOffer), pageable, 2);
        OfferSummaryResponseDto summaryDto1 =
                org.mockito.Mockito.mock(OfferSummaryResponseDto.class);
        OfferSummaryResponseDto summaryDto2 =
                org.mockito.Mockito.mock(OfferSummaryResponseDto.class);

        when(offerService.getFilteredOffers(filterDto, pageable)).thenReturn(offersPage);
        when(offerMapper.toOfferSummaryResponseDto(testOffer)).thenReturn(summaryDto1);
        when(offerMapper.toOfferSummaryResponseDto(secondOffer)).thenReturn(summaryDto2);

        // when
        Page<OfferSummaryResponseDto> result =
                offerUserService.getFilteredOffers(filterDto, pageable);

        // then
        verify(offerMapper, times(1)).toOfferSummaryResponseDto(testOffer);
        verify(offerMapper, times(1)).toOfferSummaryResponseDto(secondOffer);
        assertThat(result.getContent()).hasSize(2);
    }

    // ==================== Helper Methods ====================

    private JwtPrincipal createJwtPrincipal(User user) {
        return JwtPrincipal.from(user);
    }

    private OfferCreateRequestDto createTestOfferCreateRequestDto() {
        OfferCreateRequestDto dto = org.mockito.Mockito.mock(OfferCreateRequestDto.class);
        return dto;
    }

    private OfferUpdateRequestDto createTestOfferUpdateRequestDto() {
        OfferUpdateRequestDto dto = org.mockito.Mockito.mock(OfferUpdateRequestDto.class);
        return dto;
    }

    private OfferFilterRequestDto createTestOfferFilterRequestDto() {
        OfferFilterRequestDto dto = org.mockito.Mockito.mock(OfferFilterRequestDto.class);
        return dto;
    }
}
