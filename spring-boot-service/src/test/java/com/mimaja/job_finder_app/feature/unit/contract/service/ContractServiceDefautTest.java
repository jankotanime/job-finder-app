package com.mimaja.job_finder_app.feature.unit.contract.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

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
import com.mimaja.job_finder_app.feature.job.model.JobStatus;
import com.mimaja.job_finder_app.feature.job.service.JobService;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.repository.OfferRepository;
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContractServiceDefault - Unit Tests")
public class ContractServiceDefautTest {

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
        testOffer = createTestOffer();
        testOffer.setOwner(testOwner);
        testOffer.setChosenCandidate(testCandidate);
        testContract = createTestContract();
        testJob = createTestJob();
        ownerPrincipal = JwtPrincipal.from(testOwner);
        candidatePrincipal = JwtPrincipal.from(testCandidate);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    private Offer createTestOffer() {
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());
        offer.setContract(null);
        return offer;
    }

    private Contract createTestContract() {
        Contract contract = new Contract();
        contract.setId(UUID.randomUUID());
        contract.setOffer(testOffer);
        contract.setJob(null);
        contract.setContractorAcceptance(ContractStatus.WAITING);
        return contract;
    }

    private Job createTestJob() {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setStatus(JobStatus.READY);
        job.setOwner(testOwner);
        job.setContractor(testCandidate);
        return job;
    }

    @Test
    @DisplayName("Should upload contract successfully")
    void testUploadContract_WithValidOfferAndFile_ShouldReturnContractDto() {
        UUID offerId = testOffer.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);
        ContractCFRequestDto cfRequestDto = org.mockito.Mockito.mock(ContractCFRequestDto.class);

        when(contractFileManager.saveContract(file)).thenReturn(cfRequestDto);
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        ContractDto result = contractService.uploadContract(requestDto, ownerPrincipal);

        assertNotNull(result, "ContractDto should not be null");
        verify(offerRepository, times(1)).findById(offerId);
        verify(contractFileManager, times(1)).saveContract(file);
        verify(offerService, times(1)).attachContract(any(Offer.class), any(Contract.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when offer not found")
    void testUploadContract_WithNonExistentOffer_ShouldThrowBusinessException() {
        UUID offerId = UUID.randomUUID();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);

        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

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
    @DisplayName("Should throw BusinessException when chosen candidate is null")
    void testGetContractByOfferId_WithNullChosenCandidate_ShouldThrowBusinessException() {
        UUID offerId = testOffer.getId();
        testOffer.setChosenCandidate(null);
        testOffer.setContract(testContract);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

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

    @Test
    @DisplayName("Should throw BusinessException when user is not contractor or owner with job")
    void testGetContract_WithJobButNonAuthorizedUser_ShouldThrowBusinessException() {
        UUID contractId = testContract.getId();
        testContract.setOffer(null);
        testContract.setJob(testJob);
        JwtPrincipal unauthorizedPrincipal = JwtPrincipal.from(createTestUser());

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

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

    @Test
    @DisplayName("Should get contract successfully as contractor with job when contract and job are both null")
    void testGetContract_WithNullJobAndNullOffer_ShouldThrowBusinessException() {
        UUID contractId = testContract.getId();
        testContract.setOffer(null);
        testContract.setJob(null);

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

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
    @DisplayName("Should throw BusinessException when offer already has contract")
    void testUploadContract_WithOfferAlreadyHasContract_ShouldThrowBusinessException() {
        UUID offerId = testOffer.getId();
        testOffer.setContract(testContract);
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

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
    @DisplayName("Should throw BusinessException when user is not owner")
    void testUploadContract_WithNonOwnerUser_ShouldThrowBusinessException() {
        UUID offerId = testOffer.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);
        JwtPrincipal nonOwnerPrincipal = JwtPrincipal.from(createTestUser());

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

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
    @DisplayName("Should throw BusinessException when offer has no chosen candidate")
    void testUploadContract_WithNoChosenCandidate_ShouldThrowBusinessException() {
        UUID offerId = testOffer.getId();
        testOffer.setChosenCandidate(null);
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUploadRequestDto requestDto = new ContractUploadRequestDto(offerId, file);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

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

    @Test
    @DisplayName("Should update contract successfully")
    void testUpdateContract_WithValidContractAndFile_ShouldReturnUpdatedContractDto() {
        UUID contractId = testContract.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUpdateRequestDto requestDto = new ContractUpdateRequestDto(file);
        ContractCFRequestDto cfRequestDto = org.mockito.Mockito.mock(ContractCFRequestDto.class);

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(contractFileManager.saveContract(file)).thenReturn(cfRequestDto);

        ContractDto result = contractService.updateContract(contractId, requestDto, ownerPrincipal);

        assertNotNull(result, "ContractDto should not be null");
        verify(contractRepository, times(1)).findById(contractId);
        verify(contractFileManager, times(1)).saveContract(file);
        verify(offerService, times(1)).removeContractByOffer(any(Offer.class));
        verify(offerService, times(1)).attachContract(any(Offer.class), any(Contract.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when contract not found during update")
    void testUpdateContract_WithNonExistentContract_ShouldThrowBusinessException() {
        UUID contractId = UUID.randomUUID();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUpdateRequestDto requestDto = new ContractUpdateRequestDto(file);

        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

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
        UUID contractId = testContract.getId();
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
        ContractUpdateRequestDto requestDto = new ContractUpdateRequestDto(file);
        JwtPrincipal nonOwnerPrincipal = JwtPrincipal.from(createTestUser());

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

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

    @Test
    @DisplayName("Should accept contract successfully")
    void testAcceptContract_WithValidContractAndCandidate_ShouldReturnJobResponseDto() {
        UUID contractId = testContract.getId();
        JobResponseDto expectedJobDto = org.mockito.Mockito.mock(JobResponseDto.class);

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(jobService.createJob(testOffer)).thenReturn(testJob);
        when(jobMapper.toResponseDto(testJob)).thenReturn(expectedJobDto);
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        JobResponseDto result = contractService.acceptContract(contractId, candidatePrincipal);

        assertNotNull(result, "JobResponseDto should not be null");
        assertThat(testContract.getContractorAcceptance()).isEqualTo(ContractStatus.ACCEPTED);
        verify(contractRepository, times(1)).findById(contractId);
        verify(jobService, times(1)).createJob(testOffer);
        verify(contractRepository, times(1)).save(any(Contract.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when contract not found during accept")
    void testAcceptContract_WithNonExistentContract_ShouldThrowBusinessException() {
        UUID contractId = UUID.randomUUID();

        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

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
        UUID contractId = testContract.getId();
        JwtPrincipal nonCandidatePrincipal = JwtPrincipal.from(createTestUser());

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

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

    @Test
    @DisplayName("Should decline contract successfully")
    void testDeclineContract_WithValidContractAndCandidate_ShouldDeclineContract() {
        UUID contractId = testContract.getId();

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));
        when(contractRepository.save(any(Contract.class))).thenReturn(testContract);

        contractService.declineContract(contractId, candidatePrincipal);

        assertThat(testContract.getContractorAcceptance()).isEqualTo(ContractStatus.DECLINED);
        verify(contractRepository, times(1)).findById(contractId);
        verify(contractRepository, times(1)).save(any(Contract.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not candidate during decline")
    void testDeclineContract_WithNonCandidateUser_ShouldThrowBusinessException() {
        UUID contractId = testContract.getId();
        JwtPrincipal nonCandidatePrincipal = JwtPrincipal.from(createTestUser());

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

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

    @Test
    @DisplayName("Should get contract successfully as owner with active offer")
    void testGetContract_WithValidContractAndOwner_ShouldReturnContractDto() {
        UUID contractId = testContract.getId();

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        ContractDto result = contractService.getContract(contractId, ownerPrincipal);

        assertNotNull(result, "ContractDto should not be null");
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should get contract successfully as candidate with active offer")
    void testGetContract_WithValidContractAndCandidate_ShouldReturnContractDto() {
        UUID contractId = testContract.getId();

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        ContractDto result = contractService.getContract(contractId, candidatePrincipal);

        assertNotNull(result, "ContractDto should not be null");
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should get contract successfully as contractor with job")
    void testGetContract_WithValidContractJobAndContractor_ShouldReturnContractDto() {
        UUID contractId = testContract.getId();
        testContract.setOffer(null);
        testContract.setJob(testJob);

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        ContractDto result = contractService.getContract(contractId, candidatePrincipal);

        assertNotNull(result, "ContractDto should not be null");
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should get contract successfully as owner with job")
    void testGetContract_WithValidContractJobAndOwner_ShouldReturnContractDto() {
        UUID contractId = testContract.getId();
        testContract.setOffer(null);
        testContract.setJob(testJob);

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        ContractDto result = contractService.getContract(contractId, ownerPrincipal);

        assertNotNull(result, "ContractDto should not be null");
        verify(contractRepository, times(1)).findById(contractId);
    }

    @Test
    @DisplayName("Should throw BusinessException when contract not found during get")
    void testGetContract_WithNonExistentContract_ShouldThrowBusinessException() {
        UUID contractId = UUID.randomUUID();

        when(contractRepository.findById(contractId)).thenReturn(Optional.empty());

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
    @DisplayName("Should throw BusinessException when user has no access to contract")
    void testGetContract_WithNonAuthorizedUser_ShouldThrowBusinessException() {
        UUID contractId = testContract.getId();
        JwtPrincipal unauthorizedPrincipal = JwtPrincipal.from(createTestUser());

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

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
    @DisplayName("Should get contract by offer id successfully as owner")
    void testGetContractByOfferId_WithValidOfferIdAndOwner_ShouldReturnContractDto() {
        UUID offerId = testOffer.getId();
        testOffer.setContract(testContract);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

        ContractDto result = contractService.getContractByOfferId(offerId, ownerPrincipal);

        assertNotNull(result, "ContractDto should not be null");
        verify(offerRepository, times(1)).findById(offerId);
    }

    @Test
    @DisplayName("Should throw BusinessException when offer not found")
    void testGetContractByOfferId_WithNonExistentOffer_ShouldThrowBusinessException() {
        UUID offerId = UUID.randomUUID();

        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

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
        UUID offerId = testOffer.getId();
        testOffer.setContract(null);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

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
    @DisplayName("Should throw BusinessException when user has no access to contract in offer")
    void testGetContractByOfferId_WithNonAuthorizedUser_ShouldThrowBusinessException() {
        UUID offerId = testOffer.getId();
        testOffer.setContract(testContract);
        JwtPrincipal unauthorizedPrincipal = JwtPrincipal.from(createTestUser());

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(testOffer));

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
    @DisplayName("Should delete contract successfully")
    void testDeleteContract_WithValidContractAndOwner_ShouldDeleteContract() {
        UUID contractId = testContract.getId();

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

        contractService.deleteContract(contractId, ownerPrincipal);

        verify(contractRepository, times(1)).findById(contractId);
        verify(offerService, times(1)).removeContractByOffer(any(Offer.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not owner during delete")
    void testDeleteContract_WithNonOwnerUser_ShouldThrowBusinessException() {
        UUID contractId = testContract.getId();
        JwtPrincipal nonOwnerPrincipal = JwtPrincipal.from(createTestUser());

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

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
        UUID contractId = testContract.getId();
        testContract.setOffer(null);
        testContract.setJob(testJob);

        when(contractRepository.findById(contractId)).thenReturn(Optional.of(testContract));

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