package com.mimaja.job_finder_app.feature.cv.controller;

import com.mimaja.job_finder_app.feature.cv.service.CvService;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cv")
public class CvController {
    private final CvService cvService;
    private static final String ID = "/{cvId}";

    // Example upload
    @PostMapping("/upload")
    public ResponseEntity<ResponseDto<String>> uploadImage(
            @RequestParam("file") MultipartFile file) {
        String key = cvService.uploadFile(file);
        return ResponseEntity.ok(
                new ResponseDto<>(SuccessCode.RESOURCE_CREATED, "Successfully uploaded CV", key));
    }
}
