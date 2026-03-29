package com.mimaja.job_finder_app.feature.unit.offer.service;

import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestOfferWithOwner;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.application.mapper.ApplicationMapper;
import com.mimaja.job_finder_app.feature.contract.model.Contract;
import com.mimaja.job_finder_app.feature.contract.repository.ContractRepository;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.cv.service.CvService;
import com.mimaja.job_finder_app.feature.offer.dto.OfferApplyRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferFilterRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.mapper.OfferMapper;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.model.OfferStatus;
import com.mimaja.job_finder_app.feature.offer.repository.OfferRepository;
import com.mimaja.job_finder_app.feature.offer.service.OfferServiceDefault;
import com.mimaja.job_finder_app.feature.offer.tag.service.TagService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
import java.util.List;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("OfferServiceDefault - Unit Tests")
public class OfferServiceDefaultTest {
    @Mock private OfferRepository offerRepository;

    @Mock private OfferMapper offerMapper;

    @Mock private UserService userService;

    @Mock private TagService tagService;

    @Mock private FileManagementService fileManagementService;

    @Mock private ContractRepository contractRepository;

    @Mock private CvService cvService;

    @Mock private ApplicationMapper applicationMapper;

    private OfferServiceDefault offerService;

    private Offer testOffer;
    private User testOwner;
    private User testCandidate;

    @BeforeEach
    void setUp() {
        offerService =
                new OfferServiceDefault(
                        offerRepository,
                        offerMapper,
                        userService,
                        tagService,
                        fileManagementService,
                        contractRepository,
                        cvService,
                        applicationMapper);

        testOwner = createTestUser();
        testCandidate = createTestUser();
        testOffer = createTestOfferWithOwner(testOwner);
    }

    // ==================== Get Offer By Id Tests ====================

    @Test
    @DisplayName("Should return offer when offer exists")
    void testGetOfferById_WithExistingOffer_ShouldReturnOffer() {
        // given
        UUID offerId = testOffer.getId();
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when
        Offer result = offerService.getOfferById(offerId);

        // then
        assertNotNull(result, "Offer should not be null");
        assertThat(result.getId()).isEqualTo(offerId);
    }

    @Test
    @DisplayName("Should find offer by id before returning")
    void testGetOfferById_WithExistingOffer_ShouldFindOfferById() {
        // given
        UUID offerId = testOffer.getId();
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when
        offerService.getOfferById(offerId);

        // then
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should throw BusinessException when offer not found")
    void testGetOfferById_WithNonExistentOffer_ShouldThrowBusinessException() {
        // given
        UUID offerId = UUID.randomUUID();
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> offerService.getOfferById(offerId),
                        "Should throw BusinessException when offer not found");

        assertThat(exception.getCode())
                .as("Exception code should indicate offer not found")
                .isEqualTo(BusinessExceptionReason.OFFER_NOT_FOUND.getCode());
    }

    // ==================== Create Offer Tests ====================

    @Test
    @DisplayName("Should create offer successfully with tags and without photo")
    void testCreateOffer_WithValidDataAndNoPhoto_ShouldReturnCreatedOffer() {
        // given
        UUID userId = testOwner.getId();
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();

        when(userService.getUserById(userId)).thenReturn(testOwner);
        when(offerMapper.toEntity(requestDto)).thenReturn(testOffer);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        Offer result = offerService.createOffer(Optional.empty(), requestDto, userId);

        // then
        assertNotNull(result, "Created offer should not be null");
    }

    @Test
    @DisplayName("Should get user before creating offer")
    void testCreateOffer_WithValidDataAndNoPhoto_ShouldGetUserById() {
        // given
        UUID userId = testOwner.getId();
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();

        when(userService.getUserById(userId)).thenReturn(testOwner);
        when(offerMapper.toEntity(requestDto)).thenReturn(testOffer);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        offerService.createOffer(Optional.empty(), requestDto, userId);

        // then
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("Should save offer after setting owner and tags")
    void testCreateOffer_WithValidDataAndNoPhoto_ShouldSaveOffer() {
        // given
        UUID userId = testOwner.getId();
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();

        when(userService.getUserById(userId)).thenReturn(testOwner);
        when(offerMapper.toEntity(requestDto)).thenReturn(testOffer);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        offerService.createOffer(Optional.empty(), requestDto, userId);

        // then
        verify(offerRepository, times(1)).save(testOffer);
    }

    @Test
    @DisplayName("Should create offer with photo when photo is provided")
    void testCreateOffer_WithValidDataAndPhoto_ShouldCreateOfferWithPhoto() {
        // given
        UUID userId = testOwner.getId();
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();
        MultipartFile photo = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails fileDetails = createTestProcessedFileDetails();

        when(userService.getUserById(userId)).thenReturn(testOwner);
        when(offerMapper.toEntity(requestDto)).thenReturn(testOffer);
        when(fileManagementService.processFileDetails(any(), any())).thenReturn(fileDetails);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        offerService.createOffer(Optional.of(photo), requestDto, userId);

        // then
        verify(fileManagementService, times(1)).processFileDetails(any(), any());
        verify(fileManagementService, times(1)).uploadFile(fileDetails);
    }

    // ==================== Update Offer Tests ====================

    @Test
    @DisplayName("Should update offer successfully")
    void testUpdateOffer_WithValidDataAndNoPhoto_ShouldReturnUpdatedOffer() {
        // given
        UUID offerId = testOffer.getId();
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));
        when(offerMapper.toEntityFromUpdate(requestDto)).thenReturn(testOffer);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        Offer result = offerService.updateOffer(offerId, Optional.empty(), requestDto);

        // then
        assertNotNull(result, "Updated offer should not be null");
    }

    @Test
    @DisplayName("Should find offer before updating")
    void testUpdateOffer_WithValidDataAndNoPhoto_ShouldFindOfferById() {
        // given
        UUID offerId = testOffer.getId();
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));
        when(offerMapper.toEntityFromUpdate(requestDto)).thenReturn(testOffer);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        offerService.updateOffer(offerId, Optional.empty(), requestDto);

        // then
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should delete old photo before updating when new photo provided")
    void testUpdateOffer_WithNewPhoto_ShouldDeleteOldPhoto() {
        // given
        UUID offerId = testOffer.getId();
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();
        MultipartFile newPhoto = org.mockito.Mockito.mock(MultipartFile.class);
        ProcessedFileDetails fileDetails = createTestProcessedFileDetails();
        com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto photo =
                org.mockito.Mockito.mock(
                        com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto.class);
        when(photo.getStorageKey()).thenReturn("old-key");
        testOffer.setPhoto(photo);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));
        when(offerMapper.toEntityFromUpdate(requestDto)).thenReturn(testOffer);
        when(fileManagementService.processFileDetails(any(), any())).thenReturn(fileDetails);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        offerService.updateOffer(offerId, Optional.of(newPhoto), requestDto);

        // then
        verify(fileManagementService, times(1)).deleteFile("old-key");
    }

    @Test
    @DisplayName("Should save updated offer")
    void testUpdateOffer_WithValidDataAndNoPhoto_ShouldSaveOffer() {
        // given
        UUID offerId = testOffer.getId();
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));
        when(offerMapper.toEntityFromUpdate(requestDto)).thenReturn(testOffer);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        offerService.updateOffer(offerId, Optional.empty(), requestDto);

        // then
        verify(offerRepository, times(1)).save(testOffer);
    }

    @Test
    @DisplayName("Should throw BusinessException when offer not found during update")
    void testUpdateOffer_WithNonExistentOffer_ShouldThrowBusinessException() {
        // given
        UUID offerId = UUID.randomUUID();
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> offerService.updateOffer(offerId, Optional.empty(), requestDto),
                        "Should throw BusinessException when offer not found");

        assertThat(exception.getCode())
                .as("Exception code should indicate offer not found")
                .isEqualTo(BusinessExceptionReason.OFFER_NOT_FOUND.getCode());
    }

    // ==================== Delete Offer Tests ====================

    @Test
    @DisplayName("Should find offer before deleting")
    void testDeleteOffer_WithExistingOffer_ShouldFindOfferById() {
        // given
        UUID offerId = testOffer.getId();
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when
        offerService.deleteOffer(offerId);

        // then
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should remove contract before deleting offer")
    void testDeleteOffer_WithExistingOffer_ShouldRemoveContract() {
        // given
        UUID offerId = testOffer.getId();
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when
        offerService.deleteOffer(offerId);

        // then
        verify(contractRepository, times(0)).delete(any());
    }

    @Test
    @DisplayName("Should delete offer from repository")
    void testDeleteOffer_WithExistingOffer_ShouldDeleteOffer() {
        // given
        UUID offerId = testOffer.getId();
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when
        offerService.deleteOffer(offerId);

        // then
        verify(offerRepository, times(1)).delete(testOffer);
    }

    @Test
    @DisplayName("Should delete offer photo before deleting offer")
    void testDeleteOffer_WithOfferHasPhoto_ShouldDeletePhoto() {
        // given
        UUID offerId = testOffer.getId();
        com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto photo =
                org.mockito.Mockito.mock(
                        com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto.class);
        when(photo.getStorageKey()).thenReturn("photo-key");
        testOffer.setPhoto(photo);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when
        offerService.deleteOffer(offerId);

        // then
        verify(fileManagementService, times(1)).deleteFile("photo-key");
    }

    @Test
    @DisplayName("Should throw BusinessException when offer not found during delete")
    void testDeleteOffer_WithNonExistentOffer_ShouldThrowBusinessException() {
        // given
        UUID offerId = UUID.randomUUID();
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> offerService.deleteOffer(offerId),
                        "Should throw BusinessException when offer not found");

        assertThat(exception.getCode())
                .as("Exception code should indicate offer not found")
                .isEqualTo(BusinessExceptionReason.OFFER_NOT_FOUND.getCode());
    }

    // ==================== Delete Offers By Owner Id Tests ====================

    @Test
    @DisplayName("Should find all offers by owner id before deleting")
    void testDeleteOffersByOwnerId_WithExistingOffers_ShouldFindOffersByOwnerId() {
        // given
        UUID userId = testOwner.getId();
        List<Offer> offers = List.of(testOffer);
        when(offerRepository.findOffersByOwnerId(userId)).thenReturn(offers);

        // when
        offerService.deleteOffersByOwnerId(userId);

        // then
        verify(offerRepository, times(1)).findOffersByOwnerId(userId);
    }

    @Test
    @DisplayName("Should delete all offers by owner id")
    void testDeleteOffersByOwnerId_WithExistingOffers_ShouldDeleteAllOffers() {
        // given
        UUID userId = testOwner.getId();
        List<Offer> offers = List.of(testOffer);
        when(offerRepository.findOffersByOwnerId(userId)).thenReturn(offers);

        // when
        offerService.deleteOffersByOwnerId(userId);

        // then
        verify(offerRepository, times(1)).deleteAll(offers);
    }

    @Test
    @DisplayName("Should return empty list when owner has no offers")
    void testDeleteOffersByOwnerId_WithNoOffers_ShouldReturnEmptyList() {
        // given
        UUID userId = testOwner.getId();
        List<Offer> emptyOffers = List.of();
        when(offerRepository.findOffersByOwnerId(userId)).thenReturn(emptyOffers);

        // when
        offerService.deleteOffersByOwnerId(userId);

        // then
        verify(offerRepository, times(1)).deleteAll(emptyOffers);
    }

    @Test
    @DisplayName("Should delete photo for each offer that has photo")
    void testDeleteOffersByOwnerId_WithOffersHavePhotos_ShouldDeleteAllPhotos() {
        // given
        UUID userId = testOwner.getId();
        Offer offerWithPhoto1 = createTestOfferWithOwner(testOwner);
        Offer offerWithPhoto2 = createTestOfferWithOwner(testOwner);
        com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto photo1 =
                org.mockito.Mockito.mock(
                        com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto.class);
        com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto photo2 =
                org.mockito.Mockito.mock(
                        com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto.class);

        when(photo1.getStorageKey()).thenReturn("photo-key-1");
        when(photo2.getStorageKey()).thenReturn("photo-key-2");
        offerWithPhoto1.setPhoto(photo1);
        offerWithPhoto2.setPhoto(photo2);

        List<Offer> offers = List.of(offerWithPhoto1, offerWithPhoto2);
        when(offerRepository.findOffersByOwnerId(userId)).thenReturn(offers);

        // when
        offerService.deleteOffersByOwnerId(userId);

        // then
        verify(fileManagementService, times(1)).deleteFile("photo-key-1");
        verify(fileManagementService, times(1)).deleteFile("photo-key-2");
    }

    @Test
    @DisplayName("Should not delete file when offer has no photo")
    void testDeleteOffersByOwnerId_WithOffersWithoutPhotos_ShouldNotCallDeleteFile() {
        // given
        UUID userId = testOwner.getId();
        testOffer.setPhoto(null);
        List<Offer> offers = List.of(testOffer);
        when(offerRepository.findOffersByOwnerId(userId)).thenReturn(offers);

        // when
        offerService.deleteOffersByOwnerId(userId);

        // then
        verify(fileManagementService, times(0)).deleteFile(any());
    }

    @Test
    @DisplayName("Should delete photos only for offers that have photos")
    void testDeleteOffersByOwnerId_WithMixedOffers_ShouldDeletePhotosOnlyWhenPresent() {
        // given
        UUID userId = testOwner.getId();
        Offer offerWithPhoto = createTestOfferWithOwner(testOwner);
        Offer offerWithoutPhoto = createTestOfferWithOwner(testOwner);
        com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto photo =
                org.mockito.Mockito.mock(
                        com.mimaja.job_finder_app.feature.offer.offerphoto.model.OfferPhoto.class);

        when(photo.getStorageKey()).thenReturn("photo-key");
        offerWithPhoto.setPhoto(photo);
        offerWithoutPhoto.setPhoto(null);

        List<Offer> offers = List.of(offerWithPhoto, offerWithoutPhoto);
        when(offerRepository.findOffersByOwnerId(userId)).thenReturn(offers);

        // when
        offerService.deleteOffersByOwnerId(userId);

        // then
        verify(fileManagementService, times(1)).deleteFile("photo-key");
        verify(offerRepository, times(1)).deleteAll(offers);
    }

    @Test
    @DisplayName("Should call getPhoto for each offer to check if photo exists")
    void testDeleteOffersByOwnerId_ShouldCheckPhotoExistenceForEachOffer() {
        // given
        UUID userId = testOwner.getId();
        Offer offer1 = createTestOfferWithOwner(testOwner);
        Offer offer2 = createTestOfferWithOwner(testOwner);
        Offer offer1Spy = org.mockito.Mockito.spy(offer1);
        Offer offer2Spy = org.mockito.Mockito.spy(offer2);

        when(offer1Spy.getPhoto()).thenReturn(null);
        when(offer2Spy.getPhoto()).thenReturn(null);

        List<Offer> offers = List.of(offer1Spy, offer2Spy);
        when(offerRepository.findOffersByOwnerId(userId)).thenReturn(offers);

        // when
        offerService.deleteOffersByOwnerId(userId);

        // then
        verify(offer1Spy, times(1)).getPhoto();
        verify(offer2Spy, times(1)).getPhoto();
    }

    @Test
    @DisplayName("Should process each offer in forEach loop")
    void testDeleteOffersByOwnerId_ShouldIterateThroughAllOffers() {
        // given
        UUID userId = testOwner.getId();
        Offer offer1 = createTestOfferWithOwner(testOwner);
        Offer offer2 = createTestOfferWithOwner(testOwner);
        Offer offer3 = createTestOfferWithOwner(testOwner);

        List<Offer> offers = List.of(offer1, offer2, offer3);
        when(offerRepository.findOffersByOwnerId(userId)).thenReturn(offers);

        // when
        offerService.deleteOffersByOwnerId(userId);

        // then
        verify(offerRepository, times(1)).deleteAll(offers);
        verify(offerRepository, times(1)).findOffersByOwnerId(userId);
    }

    @Test
    @DisplayName("Should remove contract for each offer in forEach loop")
    void testDeleteOffersByOwnerId_ShouldRemoveContractForEachOffer() {
        // given
        UUID userId = testOwner.getId();
        Offer offerWithContract1 = createTestOfferWithOwner(testOwner);
        Offer offerWithContract2 = createTestOfferWithOwner(testOwner);
        Contract contract1 = org.mockito.Mockito.mock(Contract.class);
        Contract contract2 = org.mockito.Mockito.mock(Contract.class);

        when(contract1.getStorageKey()).thenReturn("contract-key-1");
        when(contract2.getStorageKey()).thenReturn("contract-key-2");
        offerWithContract1.setContract(contract1);
        offerWithContract2.setContract(contract2);

        List<Offer> offers = List.of(offerWithContract1, offerWithContract2);
        when(offerRepository.findOffersByOwnerId(userId)).thenReturn(offers);

        // when
        offerService.deleteOffersByOwnerId(userId);

        // then
        verify(contractRepository, times(2)).delete(any(Contract.class));
        verify(fileManagementService, times(1)).deleteFile("contract-key-1");
        verify(fileManagementService, times(1)).deleteFile("contract-key-2");
    }

    @Test
    @DisplayName("Should skip photo deletion when getPhoto returns null")
    void testDeleteOffersByOwnerId_ShouldNotDeleteFileWhenGetPhotoReturnsNull() {
        // given
        UUID userId = testOwner.getId();
        Offer offerWithoutPhoto = createTestOfferWithOwner(testOwner);
        offerWithoutPhoto.setPhoto(null);

        List<Offer> offers = List.of(offerWithoutPhoto);
        when(offerRepository.findOffersByOwnerId(userId)).thenReturn(offers);

        // when
        offerService.deleteOffersByOwnerId(userId);

        // then
        verify(fileManagementService, never()).deleteFile(any());
        verify(offerRepository, times(1)).deleteAll(offers);
    }

    // ==================== Get Filtered Offers Tests ====================

    @Test
    @DisplayName("Should return filtered offers")
    void testGetFilteredOffers_WithValidFilter_ShouldReturnFilteredOffers() {
        // given
        OfferFilterRequestDto filterDto = createTestOfferFilterRequestDto();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Offer> expectedPage = new PageImpl<>(List.of(testOffer), pageable, 1);
        when(offerRepository.findAll(
                        ArgumentMatchers.<Specification<Offer>>any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // when
        Page<Offer> result = offerService.getFilteredOffers(filterDto, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).contains(testOffer);
    }

    @Test
    @DisplayName("Should return empty page when no offers match filter")
    void testGetFilteredOffers_WithNoMatches_ShouldReturnEmptyPage() {
        // given
        OfferFilterRequestDto filterDto = createTestOfferFilterRequestDto();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Offer> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(offerRepository.findAll(
                        ArgumentMatchers.<Specification<Offer>>any(), any(Pageable.class)))
                .thenReturn(emptyPage);

        // when
        Page<Offer> result = offerService.getFilteredOffers(filterDto, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    // ==================== Apply Offer Tests ====================

    @Test
    @DisplayName("Should apply to offer successfully")
    void testApplyOffer_WithValidOfferAndCandidate_ShouldReturnOfferWithApplication() {
        // given
        UUID offerId = testOffer.getId();
        UUID userId = testCandidate.getId();
        UUID cvId = UUID.randomUUID();
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(cvId);
        Cv cv = org.mockito.Mockito.mock(Cv.class);

        testOffer.setStatus(OfferStatus.OPEN);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));
        when(cvService.getCvById(cvId)).thenReturn(cv);
        when(userService.getUserById(userId)).thenReturn(testCandidate);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        Offer result = offerService.applyOffer(offerId, userId, requestDto);

        // then
        assertNotNull(result, "Offer should not be null after application");
    }

    @Test
    @DisplayName("Should find offer before applying")
    void testApplyOffer_WithValidOfferAndCandidate_ShouldFindOfferById() {
        // given
        UUID offerId = testOffer.getId();
        UUID userId = testCandidate.getId();
        UUID cvId = UUID.randomUUID();
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(cvId);
        Cv cv = org.mockito.Mockito.mock(Cv.class);

        testOffer.setStatus(OfferStatus.OPEN);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));
        when(cvService.getCvById(cvId)).thenReturn(cv);
        when(userService.getUserById(userId)).thenReturn(testCandidate);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        offerService.applyOffer(offerId, userId, requestDto);

        // then
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should throw BusinessException when offer not open")
    void testApplyOffer_WithClosedOffer_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        UUID userId = testCandidate.getId();
        OfferApplyRequestDto requestDto = new OfferApplyRequestDto(UUID.randomUUID());
        testOffer.setStatus(OfferStatus.CLOSED);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when & then
        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> offerService.applyOffer(offerId, userId, requestDto),
                        "Should throw BusinessException when offer not open");

        assertThat(exception.getCode())
                .as("Exception code should indicate offer candidates limit reached")
                .isEqualTo(BusinessExceptionReason.OFFER_CANDIDATES_LIMIT.getCode());
    }

    // ==================== Attach Contract Tests ====================

    @Test
    @DisplayName("Should attach contract to offer")
    void testAttachContract_WithValidOfferAndContract_ShouldAttachContract() {
        // given
        Contract contract = org.mockito.Mockito.mock(Contract.class);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        offerService.attachContract(testOffer, contract);

        // then
        verify(offerRepository, times(1)).save(testOffer);
    }

    // ==================== Remove Contract Tests ====================

    @Test
    @DisplayName("Should remove contract from offer and delete contract")
    void testRemoveContractByOffer_WithOfferHasContract_ShouldRemoveContract() {
        // given
        Contract contract = org.mockito.Mockito.mock(Contract.class);
        when(contract.getStorageKey()).thenReturn("contract-key");
        testOffer.setContract(contract);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // when
        offerService.removeContractByOffer(testOffer);

        // then
        verify(contractRepository, times(1)).delete(contract);
        verify(fileManagementService, times(1)).deleteFile("contract-key");
    }

    @Test
    @DisplayName("Should not remove contract when offer has no contract")
    void testRemoveContractByOffer_WithOfferHasNoContract_ShouldNotRemoveContract() {
        // given
        testOffer.setContract(null);

        // when
        offerService.removeContractByOffer(testOffer);

        // then
        verify(contractRepository, times(0)).delete(any());
    }

    // ==================== Helper Methods ====================

    private OfferCreateRequestDto createTestOfferCreateRequestDto() {
        return org.mockito.Mockito.mock(OfferCreateRequestDto.class);
    }

    private OfferUpdateRequestDto createTestOfferUpdateRequestDto() {
        return org.mockito.Mockito.mock(OfferUpdateRequestDto.class);
    }

    private OfferFilterRequestDto createTestOfferFilterRequestDto() {
        return org.mockito.Mockito.mock(OfferFilterRequestDto.class);
    }

    private ProcessedFileDetails createTestProcessedFileDetails() {
        return new ProcessedFileDetails("test-key", "test.jpg", MimeType.JPG, "test", 1024, null);
    }
}
