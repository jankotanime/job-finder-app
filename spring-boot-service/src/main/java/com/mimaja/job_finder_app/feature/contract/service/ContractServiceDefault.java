package com.mimaja.job_finder_app.feature.contract.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
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
import com.mimaja.job_finder_app.feature.offer.service.OfferService;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractServiceDefault implements ContractService {
    private final OfferRepository offerRepository;
    private final ContractRepository contractRepository;
    private final ContractFileManager contractFileManager;
    private final OfferService offerService;
    private final JobService jobService;
    private final JobMapper jobMapper;

    @Override
    @Transactional
    public ContractDto uploadContract(ContractUploadRequestDto requestDto, JwtPrincipal principal) {
        Optional<Offer> offerOptional = offerRepository.findById(requestDto.offerId());
        if (offerOptional.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.OFFER_NOT_FOUND);
        }

        Offer offer = offerOptional.get();

        if (offer.getContract() != null) {
            throw new BusinessException(BusinessExceptionReason.OFFER_HAS_CONTRACT);
        }
        throwErrorIfUserNotOwner(offer, principal.id());
        throwErrorIfOfferHasNoCandidate(offer);

        Contract contract =
                Contract.from(contractFileManager.saveContract(requestDto.file()), offer);

        offerService.attachContract(offer, contract);

        return ContractDto.from(contract);
    }

    @Override
    @Transactional
    public ContractDto updateContract(
            UUID contractId, ContractUpdateRequestDto requestDto, JwtPrincipal principal) {
        Contract contract = getOfferContract(contractId);

        Offer offer = contract.getOffer();

        throwErrorIfUserNotOwner(offer, principal.id());

        offerService.removeContractByOffer(offer);

        Contract newContract =
                Contract.from(contractFileManager.saveContract(requestDto.file()), offer);

        offerService.attachContract(offer, newContract);

        return ContractDto.from(newContract);
    }

    @Override
    @Transactional
    public JobResponseDto acceptContract(UUID contractId, JwtPrincipal principal) {
        Contract contract = getOfferContract(contractId);

        Offer offer = contract.getOffer();

        throwErrorIfUserNotCandidate(offer, principal.id());

        contract.setContractorAcceptance(ContractStatus.ACCEPTED);
        contract.setOffer(null);

        Job job = jobService.createJob(offer);

        contract.setJob(job);

        contractRepository.save(contract);

        return jobMapper.toResponseDto(job);
    }

    @Override
    @Transactional
    public void declineContract(UUID contractId, JwtPrincipal principal) {
        Contract contract = getOfferContract(contractId);
        Offer offer = contract.getOffer();

        throwErrorIfUserNotCandidate(offer, principal.id());

        contract.setContractorAcceptance(ContractStatus.DECLINED);

        contractRepository.save(contract);
    }

    @Override
    @Transactional
    public ContractDto getContract(UUID contractId, JwtPrincipal principal) {
        UUID userId = principal.id();
        Optional<Contract> contractOptional = contractRepository.findById(contractId);
        if (contractOptional.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.CONTRACT_NOT_FOUND);
        }

        Contract contract = contractOptional.get();
        Offer offer = contract.getOffer();
        if (offer != null) {
            return getContractDtoIfHasAccess(offer, contract, userId);
        }

        Job job = contract.getJob();
        if (job != null) {
            if (job.getContractor().getId().equals(userId)
                    || job.getOwner().getId().equals(userId)) {
                return ContractDto.from(contract);
            }
        }

        throw new BusinessException(BusinessExceptionReason.USER_NOT_CONTRACTOR_OR_OWNER);
    }

    @Override
    public ContractDto getContractByOfferId(UUID offerId, JwtPrincipal principal) {
        Optional<Offer> offerOptional = offerRepository.findById(offerId);
        if (offerOptional.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.OFFER_NOT_FOUND);
        }

        Offer offer = offerOptional.get();
        Contract contract = offer.getContract();

        if (contract == null) {
            throw new BusinessException(BusinessExceptionReason.OFFER_HAS_NO_CONTRACT);
        }

        UUID userId = principal.id();

        return getContractDtoIfHasAccess(offer, contract, userId);
    }

    @Override
    @Transactional
    public void deleteContract(UUID contractId, JwtPrincipal principal) {
        Contract contract = getOfferContract(contractId);
        Offer offer = contract.getOffer();
        throwErrorIfUserNotOwner(offer, principal.id());
        offerService.removeContractByOffer(offer);
    }

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

    private void throwErrorIfUserNotCandidate(Offer offer, UUID id) {
        throwErrorIfOfferHasNoCandidate(offer);
        if (!offer.getChosenCandidate().getId().equals(id)) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_CANDIDATE);
        }
    }

    private void throwErrorIfUserNotOwner(Offer offer, UUID id) {
        if (!offer.getOwner().getId().equals(id)) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_OWNER);
        }
    }

    private void throwErrorIfOfferHasNoCandidate(Offer offer) {
        if (offer.getChosenCandidate() == null) {
            throw new BusinessException(BusinessExceptionReason.OFFER_HAS_NONE_CANDIDATES);
        }
    }

    private ContractDto getContractDtoIfHasAccess(Offer offer, Contract contract, UUID userId) {
        if (offer.getOwner().getId().equals(userId)) {
            return ContractDto.from(contract);
        }
        User chosenCandidate = offer.getChosenCandidate();
        if (chosenCandidate != null) {
            if (chosenCandidate.getId().equals(userId)) {
                return ContractDto.from(contract);
            }
        }
        throw new BusinessException(BusinessExceptionReason.USER_NOT_CONTRACTOR_OR_OWNER);
    }
}
