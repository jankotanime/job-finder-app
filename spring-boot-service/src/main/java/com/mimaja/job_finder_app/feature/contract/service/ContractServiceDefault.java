package com.mimaja.job_finder_app.feature.contract.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractDevelopRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUpdateRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUploadRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.response.ContractDto;
import com.mimaja.job_finder_app.feature.contract.enums.ContractStatus;
import com.mimaja.job_finder_app.feature.contract.model.Contract;
import com.mimaja.job_finder_app.feature.contract.repository.ContractRepository;
import com.mimaja.job_finder_app.feature.contract.utils.ContractFileManager;
import com.mimaja.job_finder_app.feature.job.dto.JobResponseDto;
import com.mimaja.job_finder_app.feature.job.mapper.JobMapper;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.service.JobService;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.repository.OfferRepository;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractServiceDefault implements ContractService {
    private final OfferRepository offerRepository;
    private final ContractRepository contractRepository;
    private final ContractFileManager contractFileManager;
    private final JobService jobService;
    private final JobMapper jobMapper;

    private Contract getOfferContract(UUID contractId) {
        Optional<Contract> contractOptional = contractRepository.findById(contractId);
        if (contractOptional.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.CONTRACT_NOT_FOUND);
        }

        Contract contract = contractOptional.get();
        if (contract.getOffer() == null) {
            throw new BusinessException(BusinessExceptionReason.CONTRACT_BELONGS_TO_JOB);
        }

        return contract;
    }

    private void checkIfUserIsCandidate(Offer offer, UUID id) {
        if (offer.getChosenCandidate() == null) {
            throw new BusinessException(BusinessExceptionReason.CANDIDATE_NEED_TO_BE_CHOSEN);
        }
        if (!offer.getChosenCandidate().getId().equals(id)) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_CANDIDATE);
        }
    }

    @Override
    public ContractDto uploadContract(ContractUploadRequestDto requestDto, JwtPrincipal principal) {
        Optional<Offer> offerOptional = offerRepository.findById(requestDto.offerId());
        if (offerOptional.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.OFFER_NOT_FOUND);
        }

        Offer offer = offerOptional.get();
        if (!offer.getOwner().getId().equals(principal.id())) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_OWNER);
        }
        if (offer.getContract() != null) {
            throw new BusinessException(BusinessExceptionReason.OFFER_HAS_CONTRACT);
        }

        Contract contract =
                Contract.from(contractFileManager.saveContract(requestDto.file()), offer);

        offer.setContract(contract);
        offerRepository.save(offer);

        return new ContractDto(contract);
    }

    @Override
    public ContractDto updateContract(ContractUpdateRequestDto requestDto, JwtPrincipal principal) {
        Contract contract = getOfferContract(requestDto.contractId());

        Offer offer = contract.getOffer();

        if (!offer.getOwner().getId().equals(principal.id())) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_OWNER);
        }

        contract = Contract.from(contractFileManager.saveContract(requestDto.file()), offer);

        contractRepository.save(contract);

        return new ContractDto(contract);
    }

    @Override
    public JobResponseDto acceptContract(
            ContractDevelopRequestDto requestDto, JwtPrincipal principal) {
        Contract contract = getOfferContract(requestDto.contractId());

        Offer offer = contract.getOffer();

        checkIfUserIsCandidate(offer, principal.id());

        contract.setContractorAcceptance(ContractStatus.ACCEPTED);
        contract.setOffer(null);

        Job job = jobService.createJob(offer);

        contract.setJob(job);

        contractRepository.save(contract);

        return jobMapper.toResponseDto(job);
    }

    @Override
    public void declineContract(ContractDevelopRequestDto requestDto, JwtPrincipal principal) {
        Contract contract = getOfferContract(requestDto.contractId());
        Offer offer = contract.getOffer();

        checkIfUserIsCandidate(offer, principal.id());

        contract.setContractorAcceptance(ContractStatus.DECLINED);

        contractRepository.save(contract);
    }
}
