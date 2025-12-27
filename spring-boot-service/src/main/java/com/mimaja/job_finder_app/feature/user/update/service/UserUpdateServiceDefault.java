package com.mimaja.job_finder_app.feature.user.update.service;

import com.mimaja.job_finder_app.core.handler.exception.BusinessException;
import com.mimaja.job_finder_app.core.handler.exception.BusinessExceptionReason;
import com.mimaja.job_finder_app.feature.user.model.User;
import com.mimaja.job_finder_app.feature.user.profilephoto.dto.ProfilePhotoCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.profilephoto.model.ProfilePhoto;
import com.mimaja.job_finder_app.feature.user.profilephoto.repository.ProfilePhotoRepository;
import com.mimaja.job_finder_app.feature.user.repository.UserRepository;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePhoneNumberRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateUserDataRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateEmailResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdatePhoneNumberResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateUserDataResponseDto;
import com.mimaja.job_finder_app.feature.user.update.utils.CheckDataValidity;
import com.mimaja.job_finder_app.security.configuration.PasswordConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.configuration.JwtConfiguration;
import com.mimaja.job_finder_app.security.tokens.jwt.shared.JwtPrincipal;
import com.mimaja.job_finder_app.shared.adapters.MultipartFileSource;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.FileFolderName;
import com.mimaja.job_finder_app.shared.service.FileManagementService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserUpdateServiceDefault implements UserUpdateService {
    private final CheckDataValidity checkDataValidity;
    private final PasswordConfiguration passwordConfiguration;
    private final UserRepository userRepository;
    private final ProfilePhotoRepository profilePhotoRepository;
    private final JwtConfiguration jwtConfiguration;
    private final FileManagementService fileManagementService;

    @Override
    @Transactional
    public UpdateUserDataResponseDto updateUserdata(
            Optional<MultipartFile> profilePhoto,
            UpdateUserDataRequestDto reqData,
            JwtPrincipal principal) {
        String newUsername = reqData.newUsername();
        String newFirstName = reqData.newFirstName();
        String newLastName = reqData.newLastName();
        String newProfileDescription = reqData.newProfileDescription();

        User user = principal.user();

        if (!passwordConfiguration.verifyPassword(reqData.password(), user.getPasswordHash())) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PASSWORD);
        }

        checkDataValidity.checkUsername(user.getId(), newUsername);
        checkDataValidity.checkRestData(newFirstName);
        checkDataValidity.checkRestData(newLastName);
        checkDataValidity.checkRestData(newProfileDescription);

        user.setUsername(newUsername);
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        user.setProfileDescription(newProfileDescription);

        if (user.getProfilePhoto() != null) {
            fileManagementService.deleteFile(user.getProfilePhoto().getStorageKey());
            profilePhotoRepository.deleteById(user.getProfilePhoto().getId());
            user.setProfilePhoto(null);
        }

        if (profilePhoto.isPresent()) {
            ProfilePhoto newProfilePhoto = processPhoto(profilePhoto.get());
            user.setProfilePhoto(newProfilePhoto);
        }

        userRepository.save(user);

        String accessToken = jwtConfiguration.createToken(user);

        return new UpdateUserDataResponseDto(accessToken);
    }

    @Override
    @Transactional
    public UpdatePhoneNumberResponseDto updatePhoneNumber(
            UpdatePhoneNumberRequestDto reqData, JwtPrincipal principal) {
        int newPhoneNumber = reqData.newPhoneNumber();
        User user = principal.user();

        checkDataValidity.checkPhoneNumber(user.getId(), newPhoneNumber);

        if (!passwordConfiguration.verifyPassword(reqData.password(), user.getPasswordHash())) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PASSWORD);
        }

        user.setPhoneNumber(newPhoneNumber);

        userRepository.save(user);

        String accessToken = jwtConfiguration.createToken(user);

        return new UpdatePhoneNumberResponseDto(accessToken);
    }

    @Override
    @Transactional
    public UpdateEmailResponseDto updateEmail(
            UpdateEmailRequestDto reqData, JwtPrincipal principal) {
        String newEmail = reqData.newEmail();
        User user = principal.user();

        checkDataValidity.checkEmail(user.getId(), newEmail);

        if (!passwordConfiguration.verifyPassword(reqData.password(), user.getPasswordHash())) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PASSWORD);
        }

        user.setEmail(newEmail);

        userRepository.save(user);

        String accessToken = jwtConfiguration.createToken(user);

        return new UpdateEmailResponseDto(accessToken);
    }

    private ProfilePhoto processPhoto(MultipartFile photo) {
        MultipartFileSource fileSource = new MultipartFileSource(photo);
        String folder = FileFolderName.PHOTOS.getFullPath(FileFolderName.PROFILE_PHOTOS);
        ProcessedFileDetails fileDetails =
                fileManagementService.processFileDetails(fileSource, folder);

        ProfilePhotoCreateRequestDto dto = ProfilePhotoCreateRequestDto.from(fileDetails);
        return ProfilePhoto.from(dto);
    }
}
