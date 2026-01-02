package com.mimaja.job_finder_app.feature.contract.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.contract.dto.cloudflare.ContractUploadCFRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractDevelopRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUpdateRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.request.ContractUploadRequestDto;
import com.mimaja.job_finder_app.feature.contract.dto.response.ContractDto;
import com.mimaja.job_finder_app.feature.contract.model.Contract;
import com.mimaja.job_finder_app.feature.contract.repository.ContractRepository;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.offer.repository.OfferRepository;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.shared.adapters.MultipartFileSource;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.FileFolderName;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractServiceDefault implements ContractService {
    private final OfferRepository offerRepository;
    private final ContractRepository contractRepository;
    private final FileManagementService fileManagementService;

    @Override
    public ContractDto uploadContract(ContractUploadRequestDto requestDto, JwtPrincipal principal) {
        User user = principal.user();
        Optional<Offer> offerOptional = offerRepository.findById(requestDto.offerId());
        if (offerOptional.isEmpty()) {
            throw new BusinessException(BusinessExceptionReason.OFFER_NOT_FOUND);
        }

        Offer offer = offerOptional.get();
        if (!offer.getOwner().getId().equals(user.getId())) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_OWNER);
        }

        MultipartFileSource fileSource = new MultipartFileSource(requestDto.file());
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CONTRACTS);
        ProcessedFileDetails fileDetails =
                fileManagementService.processFileDetails(fileSource, folder);

        fileManagementService.uploadFile(fileDetails);

        ContractUploadCFRequestDto dto = ContractUploadCFRequestDto.from(fileDetails, offer);

        Contract contract = Contract.from(dto);

        contractRepository.save(contract);

        return new ContractDto(contract);
    }

    @Override
    public void updateContract(ContractUpdateRequestDto requestDto, JwtPrincipal principal) {}

    @Override
    public void acceptContract(ContractDevelopRequestDto requestDto, JwtPrincipal principal) {
        // TODO Auto-generated method stub
    }

    @Override
    public void declineContract(ContractDevelopRequestDto requestDto, JwtPrincipal principal) {
        // TODO Auto-generated method stub
    }
}
