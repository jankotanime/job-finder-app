package com.mimaja.job_finder_app.feature.cv.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.cv.dto.CvResponseDto;
import com.mimaja.job_finder_app.feature.cv.mapper.CvMapper;
import com.mimaja.job_finder_app.feature.cv.model.Cv;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.service.UserService;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CvUserService {
    private final CvService cvService;
    private final UserService userService;
    private final CvMapper cvMapper;

    public CvResponseDto uploadCv(MultipartFile file, JwtPrincipal jwt) {
        return cvMapper.toResponseDto(cvService.uploadCv(file, jwt.id()));
    }

    public CvResponseDto getCvById(UUID cvId) {
        return cvMapper.toResponseDto(cvService.getCvById(cvId));
    }

    public List<CvResponseDto> getCvsByUserId(UUID userId) {
        return cvService.getCvsByUserId(userId).stream().map(cvMapper::toResponseDto).toList();
    }

    public CvResponseDto updateCv(MultipartFile file, JwtPrincipal jwt, UUID cvId) {
        throwErrorIfUserIsNotOwner(jwt.id(), cvId);
        return cvMapper.toResponseDto(cvService.updateCv(file, cvId));
    }

    public void deleteCv(JwtPrincipal jwt, UUID cvId) {
        throwErrorIfUserIsNotOwner(jwt.id(), cvId);
        cvService.deleteCv(cvId);
    }

    public void deleteAllCvsForUser(JwtPrincipal jwt) {
        cvService.deleteAllCvsForUser(jwt.id());
    }

    private void throwErrorIfUserIsNotOwner(UUID userId, UUID cvId) {
        User user = userService.getUserById(userId);
        Cv cv = cvService.getCvById(cvId);
        if (!cv.getUser().getId().equals(user.getId())) {
            throw new BusinessException(BusinessExceptionReason.USER_NOT_OWNER);
        }
    }
}
