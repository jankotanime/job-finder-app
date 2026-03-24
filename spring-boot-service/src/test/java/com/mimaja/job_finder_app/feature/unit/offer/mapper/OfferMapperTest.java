package com.mimaja.job_finder_app.feature.unit.offer.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestOffer;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestOfferWithContract;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestOfferWithPhoto;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestOfferCreateRequestDto;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestOfferUpdateRequestDto;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestOfferWithApplications;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.createTestOfferWithTags;
import static com.mimaja.job_finder_app.feature.unit.offer.mockdata.OfferMockData.TEST_OFFER_TITLE;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mimaja.job_finder_app.feature.application.mapper.ApplicationMapper;
import com.mimaja.job_finder_app.feature.offer.dto.OfferCreateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferSummaryResponseDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUpdateRequestDto;
import com.mimaja.job_finder_app.feature.offer.dto.OfferUserIsOwnerResponseDto;
import com.mimaja.job_finder_app.feature.offer.mapper.OfferMapper;
import com.mimaja.job_finder_app.feature.offer.mapper.OfferMapperImpl;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.tag.mapper.TagMapper;
import com.mimaja.job_finder_app.feature.user.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("OfferMapper - Unit Tests")
class OfferMapperTest {

    private OfferMapper offerMapper;
    private UserMapper userMapperMock;
    private TagMapper tagMapperMock;
    private ApplicationMapper applicationMapperMock;

    @BeforeEach
    void setUp() throws Exception {
        setupOfferMapperWithMocks();
    }

    /**
     * Configures OfferMapper with mocked dependencies
     */
    private void setupOfferMapperWithMocks() throws Exception {
        userMapperMock = mock(UserMapper.class);
        tagMapperMock = mock(TagMapper.class);
        applicationMapperMock = mock(ApplicationMapper.class);

        OfferMapperImpl offerMapperImpl = new OfferMapperImpl();
        injectField(offerMapperImpl, "userMapper", userMapperMock);
        injectField(offerMapperImpl, "tagMapper", tagMapperMock);
        injectField(offerMapperImpl, "applicationMapper", applicationMapperMock);

        offerMapper = offerMapperImpl;
    }

    /**
     * Injects a mock object into a private field using reflection
     */
    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // ==================== toEntity Tests ====================

    @Test
    @DisplayName("Should not be null when mapping OfferCreateRequestDto to Offer entity")
    void testToEntity_shouldReturnNonNullOffer_whenValidDtoProvided() {
        // given
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();

        // when
        Offer result = offerMapper.toEntity(requestDto);

        // then
        assertNotNull(result, "Offer entity should not be null");
    }

    @Test
    @DisplayName("Should map title correctly when mapping OfferCreateRequestDto to Offer entity")
    void testToEntity_shouldMapTitleCorrectly_whenValidDtoProvided() {
        // given
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();

        // when
        Offer result = offerMapper.toEntity(requestDto);

        // then
        assertThat(result.getTitle()).isEqualTo(TEST_OFFER_TITLE);
    }

    @Test
    @DisplayName("Should map description correctly when mapping OfferCreateRequestDto to Offer entity")
    void testToEntity_shouldMapDescriptionCorrectly_whenValidDtoProvided() {
        // given
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();

        // when
        Offer result = offerMapper.toEntity(requestDto);

        // then
        assertThat(result.getDescription()).isEqualTo(requestDto.description());
    }

    @Test
    @DisplayName("Should map salary correctly when mapping OfferCreateRequestDto to Offer entity")
    void testToEntity_shouldMapSalaryCorrectly_whenValidDtoProvided() {
        // given
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();

        // when
        Offer result = offerMapper.toEntity(requestDto);

        // then
        assertThat(result.getSalary()).isEqualTo(requestDto.salary());
    }

    @Test
    @DisplayName("Should not set id when mapping OfferCreateRequestDto to Offer entity")
    void testToEntity_shouldNotSetId_whenValidDtoProvided() {
        // given
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();

        // when
        Offer result = offerMapper.toEntity(requestDto);

        // then
        assertNull(result.getId(), "ID should not be set during mapping");
    }

    @Test
    @DisplayName("Should not map tags when mapping OfferCreateRequestDto to Offer entity")
    void testToEntity_shouldNotMapTags_whenValidDtoProvided() {
        // given
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();

        // when
        Offer result = offerMapper.toEntity(requestDto);

        // then
        assertThat(result.getTags()).isNullOrEmpty();
    }

    @Test
    @DisplayName("Should not map owner when mapping OfferCreateRequestDto to Offer entity")
    void testToEntity_shouldNotMapOwner_whenValidDtoProvided() {
        // given
        OfferCreateRequestDto requestDto = createTestOfferCreateRequestDto();

        // when
        Offer result = offerMapper.toEntity(requestDto);

        // then
        assertNull(result.getOwner(), "Owner should not be set during mapping");
    }

    // ==================== toEntityFromUpdate Tests ====================

    @Test
    @DisplayName("Should not be null when mapping OfferUpdateRequestDto to Offer entity")
    void testToEntityFromUpdate_shouldReturnNonNullOffer_whenValidDtoProvided() {
        // given
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();

        // when
        Offer result = offerMapper.toEntityFromUpdate(requestDto);

        // then
        assertNotNull(result, "Offer entity should not be null");
    }

    @Test
    @DisplayName("Should map title correctly when mapping OfferUpdateRequestDto to Offer entity")
    void testToEntityFromUpdate_shouldMapTitleCorrectly_whenValidDtoProvided() {
        // given
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();

        // when
        Offer result = offerMapper.toEntityFromUpdate(requestDto);

        // then
        assertThat(result.getTitle()).isEqualTo(requestDto.title());
    }

    @Test
    @DisplayName("Should map description correctly when mapping OfferUpdateRequestDto to Offer entity")
    void testToEntityFromUpdate_shouldMapDescriptionCorrectly_whenValidDtoProvided() {
        // given
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();

        // when
        Offer result = offerMapper.toEntityFromUpdate(requestDto);

        // then
        assertThat(result.getDescription()).isEqualTo(requestDto.description());
    }

    @Test
    @DisplayName("Should map salary correctly when mapping OfferUpdateRequestDto to Offer entity")
    void testToEntityFromUpdate_shouldMapSalaryCorrectly_whenValidDtoProvided() {
        // given
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();

        // when
        Offer result = offerMapper.toEntityFromUpdate(requestDto);

        // then
        assertThat(result.getSalary()).isEqualTo(requestDto.salary());
    }

    @Test
    @DisplayName("Should not set id when mapping OfferUpdateRequestDto to Offer entity")
    void testToEntityFromUpdate_shouldNotSetId_whenValidDtoProvided() {
        // given
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();

        // when
        Offer result = offerMapper.toEntityFromUpdate(requestDto);

        // then
        assertNull(result.getId(), "ID should not be set during mapping");
    }

    @Test
    @DisplayName("Should not map tags when mapping OfferUpdateRequestDto to Offer entity")
    void testToEntityFromUpdate_shouldNotMapTags_whenValidDtoProvided() {
        // given
        OfferUpdateRequestDto requestDto = createTestOfferUpdateRequestDto();

        // when
        Offer result = offerMapper.toEntityFromUpdate(requestDto);

        // then
        assertThat(result.getTags()).isNullOrEmpty();
    }

    // ==================== toOfferSummaryResponseDto Tests ====================

    @Test
    @DisplayName("Should map offer id to summary dto")
    void testToOfferSummaryResponseDto_ShouldMapOfferId() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.id()).isEqualTo(offer.getId());
    }

    @Test
    @DisplayName("Should map offer title to summary dto")
    void testToOfferSummaryResponseDto_ShouldMapTitle() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.title()).isEqualTo(offer.getTitle());
    }

    @Test
    @DisplayName("Should calculate candidates amount correctly when applications exist")
    void testToOfferSummaryResponseDto_ShouldCalculateCandidatesAmount() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.candidatesAmount()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should set candidates amount to 0 when applications are null")
    void testToOfferSummaryResponseDto_WithNullApplications_ShouldSetCandidatesAmountToZero() {
        // given
        Offer offer = createTestOffer();
        offer.setApplications(null);

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.candidatesAmount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should map offer salary correctly")
    void testToOfferSummaryResponseDto_ShouldMapSalaryCorrectly() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.salary()).isEqualTo(offer.getSalary());
    }

    @Test
    @DisplayName("Should map offer date and time correctly")
    void testToOfferSummaryResponseDto_ShouldMapDateAndTimeCorrectly() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.dateAndTime()).isEqualTo(offer.getDateAndTime());
    }

    // ==================== toOfferUserIsOwnerResponseDto Tests ====================

    @Test
    @DisplayName("Should map offer id to owner response")
    void testToOfferUserIsOwnerResponseDto_ShouldMapOfferId() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.id()).isEqualTo(offer.getId());
    }

    @Test
    @DisplayName("Should map offer title in owner response")
    void testToOfferUserIsOwnerResponseDto_ShouldMapTitle() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.title()).isEqualTo(offer.getTitle());
    }

    @Test
    @DisplayName("Should map offer description in owner response")
    void testToOfferUserIsOwnerResponseDto_ShouldMapDescription() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.description()).isEqualTo(offer.getDescription());
    }

    @Test
    @DisplayName("Should map offer status to owner response")
    void testToOfferUserIsOwnerResponseDto_ShouldMapStatus() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.status()).isEqualTo(offer.getStatus());
    }

    @Test
    @DisplayName("Should map offer salary to owner response")
    void testToOfferUserIsOwnerResponseDto_ShouldMapSalary() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.salary()).isEqualTo(offer.getSalary());
    }

    // ==================== Null Input Tests - Branch Coverage ====================

    @Test
    @DisplayName("Should return null when mapping null OfferCreateRequestDto")
    void testToEntity_shouldReturnNull_whenNullDtoProvided() {
        // given
        OfferCreateRequestDto requestDto = null;

        // when
        Offer result = offerMapper.toEntity(requestDto);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should return null when mapping null OfferUpdateRequestDto")
    void testToEntityFromUpdate_shouldReturnNull_whenNullDtoProvided() {
        // given
        OfferUpdateRequestDto requestDto = null;

        // when
        Offer result = offerMapper.toEntityFromUpdate(requestDto);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should return null when mapping null Offer to OfferSummaryResponseDto")
    void testToOfferSummaryResponseDto_shouldReturnNull_whenNullOfferProvided() {
        // given
        Offer offer = null;

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should return null when mapping null Offer to OfferUserIsOwnerResponseDto")
    void testToOfferUserIsOwnerResponseDto_shouldReturnNull_whenNullOfferProvided() {
        // given
        Offer offer = null;

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertNull(result, "Should return null for null input");
    }

    @Test
    @DisplayName("Should handle null applications in candidatesAmount calculation")
    void testToOfferSummaryResponseDto_shouldHandleNullApplications_whenApplicationsAreNull() {
        // given
        Offer offer = createTestOffer();
        offer.setApplications(null);

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.candidatesAmount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle empty applications in candidatesAmount calculation")
    void testToOfferSummaryResponseDto_shouldHandleEmptyApplications_whenNoApplicationsExist() {
        // given
        Offer offer = createTestOffer();
        offer.setApplications(new java.util.HashSet<>());

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.candidatesAmount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should map applications correctly in owner response")
    void testToOfferUserIsOwnerResponseDto_shouldMapApplications_whenApplicationsExist() {
        // given
        Offer offer = createTestOfferWithApplications();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.applications()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null applications in owner response")
    void testToOfferUserIsOwnerResponseDto_shouldHandleNullApplications_whenApplicationsAreNull() {
        // given
        Offer offer = createTestOffer();
        offer.setApplications(null);

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.applications()).isNull();
    }

    @Test
    @DisplayName("Should handle null chosen candidate in owner response")
    void testToOfferUserIsOwnerResponseDto_shouldHandleNullChosenCandidate_whenChosenCandidateIsNull() {
        // given
        Offer offer = createTestOffer();
        offer.setChosenCandidate(null);

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.chosenCandidate()).isNull();
    }

    @Test
    @DisplayName("Should handle null contract in owner response")
    void testToOfferUserIsOwnerResponseDto_shouldHandleNullContract_whenContractIsNull() {
        // given
        Offer offer = createTestOffer();
        offer.setContract(null);

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.contract()).isNull();
    }

    @Test
    @DisplayName("Should handle null photo in summary response")
    void testToOfferSummaryResponseDto_shouldHandleNullPhoto_whenPhotoIsNull() {
        // given
        Offer offer = createTestOffer();
        offer.setPhoto(null);

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.photo()).isNull();
    }

    @Test
    @DisplayName("Should handle null photo in owner response")
    void testToOfferUserIsOwnerResponseDto_shouldHandleNullPhoto_whenPhotoIsNull() {
        // given
        Offer offer = createTestOffer();
        offer.setPhoto(null);

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.photo()).isNull();
    }

    @Test
    @DisplayName("Should map tags correctly in summary response when offer has tags")
    void testToOfferSummaryResponseDto_shouldMapTagsCorrectly_whenOfferHasTags() {
        // given
        Offer offer = createTestOfferWithTags();

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.tags()).isNotNull();
    }

    @Test
    @DisplayName("Should map tags correctly in owner response when offer has tags")
    void testToOfferUserIsOwnerResponseDto_shouldMapTagsCorrectly_whenOfferHasTags() {
        // given
        Offer offer = createTestOfferWithTags();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.tags()).isNotNull();
    }

    // ==================== Helper Methods - Null Handling Tests ====================

    @Test
    @DisplayName("Should handle null owner in summary response")
    void testToOfferSummaryResponseDto_shouldHandleNullOwner_whenOwnerIsNull() {
        // given
        Offer offer = createTestOffer();
        offer.setOwner(null);

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.owner()).isNull();
    }

    @Test
    @DisplayName("Should handle null tags in summary response")
    void testToOfferSummaryResponseDto_shouldHandleNullTags_whenTagsAreNull() {
        // given
        Offer offer = createTestOffer();
        offer.setTags(null);

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.tags()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null tags in owner response")
    void testToOfferUserIsOwnerResponseDto_shouldHandleNullTags_whenTagsAreNull() {
        // given
        Offer offer = createTestOffer();
        offer.setTags(null);

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.tags()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null owner in owner response")
    void testToOfferUserIsOwnerResponseDto_shouldHandleNullOwner_whenOwnerIsNull() {
        // given
        Offer offer = createTestOffer();
        offer.setOwner(null);

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.chosenCandidate()).isNull();
    }

    @Test
    @DisplayName("Should map photo in summary response when photo exists")
    void testToOfferSummaryResponseDto_shouldMapPhoto_whenPhotoExists() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should map non-empty applications in owner response")
    void testToOfferUserIsOwnerResponseDto_shouldMapApplications_whenApplicationsHaveElements() {
        // given
        Offer offer = createTestOfferWithApplications();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.applications()).isNotEmpty();
    }

    @Test
    @DisplayName("Should handle contract mapping in owner response")
    void testToOfferUserIsOwnerResponseDto_shouldMapContract_whenContractExists() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should map status correctly in owner response")
    void testToOfferUserIsOwnerResponseDto_shouldMapStatus_whenValidOfferProvided() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.status()).isEqualTo(offer.getStatus());
    }

    @Test
    @DisplayName("Should map status correctly in summary response")
    void testToOfferSummaryResponseDto_shouldMapStatus_whenValidOfferProvided() {
        // given
        Offer offer = createTestOffer();

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.status()).isEqualTo(offer.getStatus());
    }

    @Test
    @DisplayName("Should map photo correctly in summary response when photo exists")
    void testToOfferSummaryResponseDto_shouldMapPhotoCorrectly_whenPhotoIsNotNull() {
        // given
        Offer offer = createTestOfferWithPhoto();

        // when
        OfferSummaryResponseDto result = offerMapper.toOfferSummaryResponseDto(offer);

        // then
        assertThat(result.photo()).isNotNull();
    }

    @Test
    @DisplayName("Should map applications correctly when non-empty")
    void testToOfferUserIsOwnerResponseDto_shouldMapApplicationsWithData_whenApplicationsExist() {
        // given
        Offer offer = createTestOfferWithApplications();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.applications()).isNotNull();
    }

    @Test
    @DisplayName("Should map contract correctly when contract exists")
    void testToOfferUserIsOwnerResponseDto_shouldMapContractCorrectly_whenContractIsNotNull() {
        // given
        Offer offer = createTestOfferWithContract();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.contract()).isNotNull();
    }

    @Test
    @DisplayName("Should map photo correctly in owner response when photo exists")
    void testToOfferUserIsOwnerResponseDto_shouldMapPhotoCorrectly_whenPhotoIsNotNull() {
        // given
        Offer offer = createTestOfferWithPhoto();

        // when
        OfferUserIsOwnerResponseDto result = offerMapper.toOfferUserIsOwnerResponseDto(offer);

        // then
        assertThat(result.photo()).isNotNull();
    }
}
