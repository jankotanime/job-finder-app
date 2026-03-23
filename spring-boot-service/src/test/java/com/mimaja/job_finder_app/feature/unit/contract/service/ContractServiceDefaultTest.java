package com.mimaja.job_finder_app.feature.unit.contract.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static com.mimaja.job_finder_app.feature.unit.contract.mockdata.ContractMockData.createTestUser;
import static com.mimaja.job_finder_app.feature.unit.contract.mockdata.ContractMockData.createTestOffer;
import static com.mimaja.job_finder_app.feature.unit.contract.mockdata.ContractMockData.createTestContract;
import static com.mimaja.job_finder_app.feature.unit.contract.mockdata.ContractMockData.createTestJob;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.contract.dto.cloudflare.ContractCFRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUpdateRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUploadRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.response.ContractDto;
import com.mimaja.job_finder_app.feature.contract.enums.ContractStatus;
import com.mimaja.job_finder_app.feature.contract.model.Contract;
import com.mimaja.job_finder_app.feature.contract.repository.ContractRepository;
import com.mimaja.job_finder_app.feature.contract.service.ContractServiceDefault;
import com.mimaja.job_finder_app.feature.contract.utils.ContractFileManager;
import com.mimaja.job_finder_app.feature.job.dto.JobResponseDto;
import com.mimaja.job_finder_app.feature.job.mapper.JobMapper;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.repository.OfferRepository;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.job.service.JobService;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContractServiceDefault - Unit Tests")
public class ContractServiceDefaultTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private ContractFileManager contractFileManager;

    @Mock
    private OfferService offerService;

    @Mock
    private JobService jobService;

    @Mock
    private JobMapper jobMapper;

    private ContractServiceDefault contractService;

    private Contract testContract;
    private Offer testOffer;
    private User testOwner;
    private User testCandidate;
    private Job testJob;
    private JwtPrincipal ownerPrincipal;
    private JwtPrincipal candidatePrincipal;

    @BeforeEach
    void setUp() {
        contractService = new ContractServiceDefault(
            offerRepository,
            contractRepository,
            contractFileManager,
            offerService,
            jobService,
            jobMapper
        );

        testOwner = createTestUser();
        testCandidate = createTestUser();
        testOffer = createTestOffer(testOwner, testCandidate);
        testOffer.setOwner(testOwner);
        testOffer.setChosenCandidate(testCandidate);
        testContract = createTestContract(testOwner, testCandidate);
        testJob = createTestJob(testOwner, testCandidate);
        ownerPrincipal = JwtPrincipal.from(testOwner);
        candidatePrincipal = JwtPrincipal.from(testCandidate);
    }

    // ==================== Upload Contract Tests ====================

    @Test
    @DisplayName("Should return ContractDto when uploading contract with valid offer and file")
    void testUploadContract_WithValidOfferAndFile_ShouldReturnContractDto() {
        // given
        UUID offerId = testOffer.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);
        ContractCFRequestDto cfRequestDto = org.mockito.Mockito.mock(ContractCFRequestDto.class);
        when(contractFileManager.saveContract(file)).thenReturn(cfRequestDto);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when
        ContractDto result = contractService.uploadContract(requestDto, ownerPrincipal);

        // then
        assertNotNull(result, "ContractDto should not be null");
    }

    @Test
    @DisplayName("Should find offer by id before uploading contract")
    void testUploadContract_WithValidOfferAndFile_ShouldFindOfferById() {
        // given
        UUID offerId = testOffer.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);
        ContractCFRequestDto cfRequestDto = org.mockito.Mockito.mock(ContractCFRequestDto.class);
        when(contractFileManager.saveContract(file)).thenReturn(cfRequestDto);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when
        contractService.uploadContract(requestDto, ownerPrincipal);

        // then
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should save contract file before uploading")
    void testUploadContract_WithValidOfferAndFile_ShouldSaveContractFile() {
        // given
        UUID offerId = testOffer.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);
        ContractCFRequestDto cfRequestDto = org.mockito.Mockito.mock(ContractCFRequestDto.class);
        when(contractFileManager.saveContract(file)).thenReturn(cfRequestDto);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when
        contractService.uploadContract(requestDto, ownerPrincipal);

        // then
        verify(contractFileManager, times(1)).saveContract(file);
    }

    @Test
    @DisplayName("Should attach contract to offer when uploading")
    void testUploadContract_WithValidOfferAndFile_ShouldAttachContractToOffer() {
        // given
        UUID offerId = testOffer.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);
        ContractCFRequestDto cfRequestDto = org.mockito.Mockito.mock(ContractCFRequestDto.class);
        when(contractFileManager.saveContract(file)).thenReturn(cfRequestDto);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when
        contractService.uploadContract(requestDto, ownerPrincipal);

        // then
        verify(offerService, times(1)).attachContract(any(Offer.class), any(Contract.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when offer not found during upload")
    void testUploadContract_WithNonExistentOffer_ShouldThrowBusinessException() {
        // given
        UUID offerId = UUID.randomUUID();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.uploadContract(requestDto, ownerPrincipal),
            "Should throw BusinessException when offer not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate offer not found")
            .isEqualTo(BusinessExceptionReason.OFFER_NOT_FOUND.getCode());
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should throw BusinessException when offer already has contract")
    void testUploadContract_WithOfferAlreadyHasContract_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        testOffer.setContract(testContract);
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.uploadContract(requestDto, ownerPrincipal),
            "Should throw BusinessException when offer already has contract"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate offer has contract")
            .isEqualTo(BusinessExceptionReason.OFFER_HAS_CONTRACT.getCode());
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not owner during upload")
    void testUploadContract_WithNonOwnerUser_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);
        JwtPrincipal nonOwnerPrincipal = JwtPrincipal.from(createTestUser());
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.uploadContract(requestDto, nonOwnerPrincipal),
            "Should throw BusinessException when user is not owner"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_OWNER.getCode());
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should throw BusinessException when offer has no chosen candidate during upload")
    void testUploadContract_WithNoChosenCandidate_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        testOffer.setChosenCandidate(null);
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.uploadContract(requestDto, ownerPrincipal),
            "Should throw BusinessException when no candidate chosen"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate no candidates")
            .isEqualTo(BusinessExceptionReason.OFFER_HAS_NONE_CANDIDATES.getCode());
        verify(offerRepository, times(1)).findById(offerId);
    }

    // ==================== Update Contract Tests ====================

    @Test
    @DisplayName("Should return updated ContractDto when updating contract with valid data")
    void testUpdateContract_WithValidContractAndFile_ShouldReturnUpdatedContractDto() {
        // given
        UUID contractId = testContract.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUpdateRequestDto requestDto = new ContractUpdateRequestDto(file);
        ContractCFRequestDto cfRequestDto = org.mockito.Mockito.mock(ContractCFRequestDto.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(contractFileManager.saveContract(file)).thenReturn(cfRequestDto);

        // when
        ContractDto result = contractService.updateContract(contractId, requestDto, ownerPrincipal);

        // then
        assertNotNull(result, "ContractDto should not be null");
    }

    @Test
    @DisplayName("Should find contract before updating")
    void testUpdateContract_WithValidContractAndFile_ShouldFindContractById() {
        // given
        UUID contractId = testContract.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUpdateRequestDto requestDto = new ContractUpdateRequestDto(file);
        ContractCFRequestDto cfRequestDto = org.mockito.Mockito.mock(ContractCFRequestDto.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(contractFileManager.saveContract(file)).thenReturn(cfRequestDto);

        // when
        contractService.updateContract(contractId, requestDto, ownerPrincipal);

        // then
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should save updated contract file")
    void testUpdateContract_WithValidContractAndFile_ShouldSaveUpdatedFile() {
        // given
        UUID contractId = testContract.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUpdateRequestDto requestDto = new ContractUpdateRequestDto(file);
        ContractCFRequestDto cfRequestDto = org.mockito.Mockito.mock(ContractCFRequestDto.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(contractFileManager.saveContract(file)).thenReturn(cfRequestDto);

        // when
        contractService.updateContract(contractId, requestDto, ownerPrincipal);

        // then
        verify(contractFileManager, times(1)).saveContract(file);
    }

    @Test
    @DisplayName("Should remove old contract before updating")
    void testUpdateContract_WithValidContractAndFile_ShouldRemoveOldContract() {
        // given
        UUID contractId = testContract.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUpdateRequestDto requestDto = new ContractUpdateRequestDto(file);
        ContractCFRequestDto cfRequestDto = org.mockito.Mockito.mock(ContractCFRequestDto.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(contractFileManager.saveContract(file)).thenReturn(cfRequestDto);

        // when
        contractService.updateContract(contractId, requestDto, ownerPrincipal);

        // then
        verify(offerService, times(1)).removeContractByOffer(any(Offer.class));
    }

    @Test
    @DisplayName("Should attach updated contract to offer")
    void testUpdateContract_WithValidContractAndFile_ShouldAttachUpdatedContract() {
        // given
        UUID contractId = testContract.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUpdateRequestDto requestDto = new ContractUpdateRequestDto(file);
        ContractCFRequestDto cfRequestDto = org.mockito.Mockito.mock(ContractCFRequestDto.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(contractFileManager.saveContract(file)).thenReturn(cfRequestDto);

        // when
        contractService.updateContract(contractId, requestDto, ownerPrincipal);

        // then
        verify(offerService, times(1)).attachContract(any(Offer.class), any(Contract.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when contract not found during update")
    void testUpdateContract_WithNonExistentContract_ShouldThrowBusinessException() {
        // given
        UUID contractId = UUID.randomUUID();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUpdateRequestDto requestDto = new ContractUpdateRequestDto(file);
        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.updateContract(contractId, requestDto, ownerPrincipal),
            "Should throw BusinessException when contract not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate contract not found")
            .isEqualTo(BusinessExceptionReason.CONTRACT_NOT_FOUND.getCode());
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not owner during update")
    void testUpdateContract_WithNonOwnerUser_ShouldThrowBusinessException() {
        // given
        UUID contractId = testContract.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUpdateRequestDto requestDto = new ContractUpdateRequestDto(file);
        JwtPrincipal nonOwnerPrincipal = JwtPrincipal.from(createTestUser());
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.updateContract(contractId, requestDto, nonOwnerPrincipal),
            "Should throw BusinessException when user is not owner"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_OWNER.getCode());
        verify(contractRepository, times(1)).findById(contractId);
    }

    // ==================== Accept Contract Tests ====================

    @Test
    @DisplayName("Should return JobResponseDto when accepting contract with valid data")
    void testAcceptContract_WithValidContractAndCandidate_ShouldReturnJobResponseDto() {
        // given
        UUID contractId = testContract.getId();
        JobResponseDto expectedJobDto = org.mockito.Mockito.mock(JobResponseDto.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(jobService.createJob(any(Offer.class))).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(expectedJobDto);
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        // when
        JobResponseDto result = contractService.acceptContract(contractId, candidatePrincipal);

        // then
        assertNotNull(result, "JobResponseDto should not be null");
    }

    @Test
    @DisplayName("Should set contractor acceptance to ACCEPTED when accepting")
    void testAcceptContract_WithValidContractAndCandidate_ShouldSetAcceptanceStatusToAccepted() {
        // given
        UUID contractId = testContract.getId();
        JobResponseDto expectedJobDto = org.mockito.Mockito.mock(JobResponseDto.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(jobService.createJob(any(Offer.class))).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(expectedJobDto);
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        // when
        contractService.acceptContract(contractId, candidatePrincipal);

        // then
        assertThat(testContract.getContractorAcceptance()).isEqualTo(ContractStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Should find contract before accepting")
    void testAcceptContract_WithValidContractAndCandidate_ShouldFindContractById() {
        // given
        UUID contractId = testContract.getId();
        JobResponseDto expectedJobDto = org.mockito.Mockito.mock(JobResponseDto.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(jobService.createJob(any(Offer.class))).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(expectedJobDto);
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        // when
        contractService.acceptContract(contractId, candidatePrincipal);

        // then
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should create job when accepting contract")
    void testAcceptContract_WithValidContractAndCandidate_ShouldCreateJob() {
        // given
        UUID contractId = testContract.getId();
        JobResponseDto expectedJobDto = org.mockito.Mockito.mock(JobResponseDto.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(jobService.createJob(any(Offer.class))).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(expectedJobDto);
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        // when
        contractService.acceptContract(contractId, candidatePrincipal);

        // then
        verify(jobService, times(1)).createJob(any(Offer.class));
    }

    @Test
    @DisplayName("Should save contract after accepting")
    void testAcceptContract_WithValidContractAndCandidate_ShouldSaveContract() {
        // given
        UUID contractId = testContract.getId();
        JobResponseDto expectedJobDto = org.mockito.Mockito.mock(JobResponseDto.class);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(jobService.createJob(any(Offer.class))).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(expectedJobDto);
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        // when
        contractService.acceptContract(contractId, candidatePrincipal);

        // then
        verify(contractRepository, times(1)).save(any(Contract.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when contract not found during accept")
    void testAcceptContract_WithNonExistentContract_ShouldThrowBusinessException() {
        // given
        UUID contractId = UUID.randomUUID();
        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.acceptContract(contractId, candidatePrincipal),
            "Should throw BusinessException when contract not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate contract not found")
            .isEqualTo(BusinessExceptionReason.CONTRACT_NOT_FOUND.getCode());
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not candidate during accept")
    void testAcceptContract_WithNonCandidateUser_ShouldThrowBusinessException() {
        // given
        UUID contractId = testContract.getId();
        JwtPrincipal nonCandidatePrincipal = JwtPrincipal.from(createTestUser());
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.acceptContract(contractId, nonCandidatePrincipal),
            "Should throw BusinessException when user is not candidate"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not candidate")
            .isEqualTo(BusinessExceptionReason.USER_NOT_CANDIDATE.getCode());
        verify(contractRepository, times(1)).findById(contractId);
    }

    // ==================== Decline Contract Tests ====================

    @Test
    @DisplayName("Should set contractor acceptance to DECLINED when declining")
    void testDeclineContract_WithValidContractAndCandidate_ShouldDeclineContract() {
        // given
        UUID contractId = testContract.getId();
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        // when
        contractService.declineContract(contractId, candidatePrincipal);

        // then
        assertThat(testContract.getContractorAcceptance()).isEqualTo(ContractStatus.DECLINED);
    }

    @Test
    @DisplayName("Should find contract before declining")
    void testDeclineContract_WithValidContractAndCandidate_ShouldFindContractById() {
        // given
        UUID contractId = testContract.getId();
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        // when
        contractService.declineContract(contractId, candidatePrincipal);

        // then
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should save contract after declining")
    void testDeclineContract_WithValidContractAndCandidate_ShouldSaveContract() {
        // given
        UUID contractId = testContract.getId();
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        // when
        contractService.declineContract(contractId, candidatePrincipal);

        // then
        verify(contractRepository, times(1)).save(any(Contract.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not candidate during decline")
    void testDeclineContract_WithNonCandidateUser_ShouldThrowBusinessException() {
        // given
        UUID contractId = testContract.getId();
        JwtPrincipal nonCandidatePrincipal = JwtPrincipal.from(createTestUser());
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.declineContract(contractId, nonCandidatePrincipal),
            "Should throw BusinessException when user is not candidate"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not candidate")
            .isEqualTo(BusinessExceptionReason.USER_NOT_CANDIDATE.getCode());
        verify(contractRepository, times(1)).findById(contractId);
    }

    // ==================== Get Contract Tests ====================

    @Test
    @DisplayName("Should return ContractDto as owner with active offer")
    void testGetContract_WithValidContractAndOwner_ShouldReturnContractDto() {
        // given
        UUID contractId = testContract.getId();
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when
        ContractDto result = contractService.getContract(contractId, ownerPrincipal);

        // then
        assertNotNull(result, "ContractDto should not be null");
    }

    @Test
    @DisplayName("Should return ContractDto as candidate with active offer")
    void testGetContract_WithValidContractAndCandidate_ShouldReturnContractDto() {
        // given
        UUID contractId = testContract.getId();
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when
        ContractDto result = contractService.getContract(contractId, candidatePrincipal);

        // then
        assertNotNull(result, "ContractDto should not be null");
    }

    @Test
    @DisplayName("Should return ContractDto as contractor with job")
    void testGetContract_WithValidContractJobAndContractor_ShouldReturnContractDto() {
        // given
        UUID contractId = testContract.getId();
        testContract.setOffer(null);
        testContract.setJob(testJob);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when
        ContractDto result = contractService.getContract(contractId, candidatePrincipal);

        // then
        assertNotNull(result, "ContractDto should not be null");
    }

    @Test
    @DisplayName("Should return ContractDto as owner with job")
    void testGetContract_WithValidContractJobAndOwner_ShouldReturnContractDto() {
        // given
        UUID contractId = testContract.getId();
        testContract.setOffer(null);
        testContract.setJob(testJob);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when
        ContractDto result = contractService.getContract(contractId, ownerPrincipal);

        // then
        assertNotNull(result, "ContractDto should not be null");
    }

    @Test
    @DisplayName("Should throw BusinessException when contract not found during get")
    void testGetContract_WithNonExistentContract_ShouldThrowBusinessException() {
        // given
        UUID contractId = UUID.randomUUID();
        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.getContract(contractId, ownerPrincipal),
            "Should throw BusinessException when contract not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate contract not found")
            .isEqualTo(BusinessExceptionReason.CONTRACT_NOT_FOUND.getCode());
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not contractor or owner")
    void testGetContract_WithNonAuthorizedUser_ShouldThrowBusinessException() {
        // given
        UUID contractId = testContract.getId();
        JwtPrincipal unauthorizedPrincipal = JwtPrincipal.from(createTestUser());
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.getContract(contractId, unauthorizedPrincipal),
            "Should throw BusinessException when user has no access"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not contractor or owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_CONTRACTOR_OR_OWNER.getCode());
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should throw BusinessException when job and offer are both null")
    void testGetContract_WithNullJobAndNullOffer_ShouldThrowBusinessException() {
        // given
        UUID contractId = testContract.getId();
        testContract.setOffer(null);
        testContract.setJob(null);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.getContract(contractId, ownerPrincipal),
            "Should throw BusinessException when both job and offer are null"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not contractor or owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_CONTRACTOR_OR_OWNER.getCode());
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should throw BusinessException when job user and contractor are different")
    void testGetContract_WithJobButNonAuthorizedUser_ShouldThrowBusinessException() {
        // given
        UUID contractId = testContract.getId();
        testContract.setOffer(null);
        testContract.setJob(testJob);
        JwtPrincipal unauthorizedPrincipal = JwtPrincipal.from(createTestUser());
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.getContract(contractId, unauthorizedPrincipal),
            "Should throw BusinessException when user is not contractor or owner"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not contractor or owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_CONTRACTOR_OR_OWNER.getCode());
        verify(contractRepository, times(1)).findById(contractId);
    }

    // ==================== Get Contract By Offer Id Tests ====================

    @Test
    @DisplayName("Should return ContractDto by offer id as owner")
    void testGetContractByOfferId_WithValidOfferIdAndOwner_ShouldReturnContractDto() {
        // given
        UUID offerId = testOffer.getId();
        testOffer.setContract(testContract);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when
        ContractDto result = contractService.getContractByOfferId(offerId, ownerPrincipal);

        // then
        assertNotNull(result, "ContractDto should not be null");
    }

    @Test
    @DisplayName("Should throw BusinessException when offer not found")
    void testGetContractByOfferId_WithNonExistentOffer_ShouldThrowBusinessException() {
        // given
        UUID offerId = UUID.randomUUID();
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.getContractByOfferId(offerId, ownerPrincipal),
            "Should throw BusinessException when offer not found"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate offer not found")
            .isEqualTo(BusinessExceptionReason.OFFER_NOT_FOUND.getCode());
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should throw BusinessException when offer has no contract")
    void testGetContractByOfferId_WithOfferHasNoContract_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        testOffer.setContract(null);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.getContractByOfferId(offerId, ownerPrincipal),
            "Should throw BusinessException when offer has no contract"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate offer has no contract")
            .isEqualTo(BusinessExceptionReason.OFFER_HAS_NO_CONTRACT.getCode());
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should throw BusinessException when user has no access to offer contract")
    void testGetContractByOfferId_WithNonAuthorizedUser_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        testOffer.setContract(testContract);
        JwtPrincipal unauthorizedPrincipal = JwtPrincipal.from(createTestUser());
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.getContractByOfferId(offerId, unauthorizedPrincipal),
            "Should throw BusinessException when user has no access"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not contractor or owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_CONTRACTOR_OR_OWNER.getCode());
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should throw BusinessException when null chosen candidate in offer")
    void testGetContractByOfferId_WithNullChosenCandidate_ShouldThrowBusinessException() {
        // given
        UUID offerId = testOffer.getId();
        testOffer.setChosenCandidate(null);
        testOffer.setContract(testContract);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.getContractByOfferId(offerId, candidatePrincipal),
            "Should throw BusinessException when chosen candidate is null"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not contractor or owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_CONTRACTOR_OR_OWNER.getCode());
        verify(offerRepository, times(1)).findById(offerId);
    }

    // ==================== Delete Contract Tests ====================

    @Test
    @DisplayName("Should find contract before deleting")
    void testDeleteContract_WithValidContractAndOwner_ShouldDeleteContract() {
        // given
        UUID contractId = testContract.getId();
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when
        contractService.deleteContract(contractId, ownerPrincipal);

        // then
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should remove contract from offer when deleting")
    void testDeleteContract_WithValidContractAndOwner_ShouldRemoveContractFromOffer() {
        // given
        UUID contractId = testContract.getId();
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when
        contractService.deleteContract(contractId, ownerPrincipal);

        // then
        verify(offerService, times(1)).removeContractByOffer(any(Offer.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not owner during delete")
    void testDeleteContract_WithNonOwnerUser_ShouldThrowBusinessException() {
        // given
        UUID contractId = testContract.getId();
        JwtPrincipal nonOwnerPrincipal = JwtPrincipal.from(createTestUser());
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.deleteContract(contractId, nonOwnerPrincipal),
            "Should throw BusinessException when user is not owner"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate user is not owner")
            .isEqualTo(BusinessExceptionReason.USER_NOT_OWNER.getCode());
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should throw BusinessException when contract belongs to job during delete")
    void testDeleteContract_WithContractBelongsToJob_ShouldThrowBusinessException() {
        // given
        UUID contractId = testContract.getId();
        testContract.setOffer(null);
        testContract.setJob(testJob);
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> contractService.deleteContract(contractId, ownerPrincipal),
            "Should throw BusinessException when contract belongs to job"
        );

        assertThat(exception.getCode())
            .as("Exception code should indicate contract belongs to job")
            .isEqualTo(BusinessExceptionReason.CONTRACT_BELONGS_TO_JOB.getCode());
        verify(contractRepository, times(1)).findById(contractId);
    }
}
