package com.mimaja.job_finder_app.feature.contract.utils;

import com.mimaja.job_finder_app.feature.contract.dto.cloudflare.ContractCFRequestDto;
import com.mimaja.job_finder_app.shared.adapters.MultipartFileSource;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.FileFolderName;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ContractFileManager {
    private final FileManagementService fileManagementService;

    public ContractCFRequestDto saveContract(MultipartFile file) {
        MultipartFileSource fileSource = new MultipartFileSource(file);
        String folder = FileFolderName.DOCUMENTS.getFullPath(FileFolderName.CONTRACTS);
        ProcessedFileDetails fileDetails =
                fileManagementService.processFileDetails(fileSource, folder);

        fileManagementService.uploadFile(fileDetails);

        ContractCFRequestDto dto = ContractCFRequestDto.from(fileDetails);

        return dto;
    }
}
