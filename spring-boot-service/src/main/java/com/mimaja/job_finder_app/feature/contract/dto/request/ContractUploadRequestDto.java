package com.mimaja.job_finder_app.feature.contract.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public record ContractUploadRequestDto(@NotNull MultipartFile file, @NotNull UUID offerId) {}
