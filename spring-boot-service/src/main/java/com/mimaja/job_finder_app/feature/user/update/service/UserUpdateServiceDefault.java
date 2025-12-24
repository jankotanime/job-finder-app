package com.mimaja.job_finder_app.feature.user.update.service;

import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationExceptionReason;
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
import com.mimaja.job_finder_app.shared.enums.MimeType;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class UserUpdateServiceDefault implements UserUpdateService {
    private final CheckDataValidity checkDataValidity;
    private final PasswordConfiguration passwordConfiguration;
    private final UserRepository userRepository;
    private final ProfilePhotoRepository profilePhotoRepository;
    private final JwtConfiguration jwtConfiguration;
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessedPhotoDetails {
        String fileName;
        String contentType;
        MimeType mimeType;
        String storageKey;
    }

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
            DeleteObjectRequest deleteObjectRequest =
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(user.getProfilePhoto().getStorageKey())
                            .build();
            s3Client.deleteObject(deleteObjectRequest);
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
        ProcessedPhotoDetails fileDetails = processFileDetails(photo);

        PutObjectRequest req =
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileDetails.storageKey)
                        .contentType(fileDetails.contentType)
                        .build();

        try {
            s3Client.putObject(req, RequestBody.fromBytes(photo.getBytes()));
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionReason.FILE_UPLOAD_EXCEPTION);
        }

        BigInteger fileSize = BigInteger.valueOf(photo.getSize());

        ProfilePhotoCreateRequestDto dto =
                new ProfilePhotoCreateRequestDto(
                        fileDetails.getFileName(),
                        fileDetails.getMimeType(),
                        fileSize,
                        fileDetails.getStorageKey());
        return ProfilePhoto.from(dto);
    }

    private MimeType getFileExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            throw new IllegalArgumentException("Invalid file extension in filename: " + filename);
        }
        return MimeType.valueOf(filename.substring(idx + 1).toUpperCase());
    }

    private String getFileName(MultipartFile file) {
        return Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(
                        () ->
                                new ApplicationException(
                                        ApplicationExceptionReason.FILE_NAME_MISSING))
                .toLowerCase();
    }

    private String getContentType(MultipartFile file) {
        return Optional.ofNullable(file.getContentType())
                .orElseThrow(
                        () ->
                                new ApplicationException(
                                        ApplicationExceptionReason.CONTENT_TYPE_UNKNOWN));
    }

    private ProcessedPhotoDetails processFileDetails(MultipartFile file) {
        String fileName = getFileName(file);
        String contentType = getContentType(file);
        MimeType ext = getFileExtension(fileName);
        if (!ext.equals(MimeType.JPG) && !ext.equals(MimeType.JPEG) && !ext.equals(MimeType.PNG)) {
            throw new BusinessException(BusinessExceptionReason.WRONG_PHOTO_FORMAT);
        }
        String folder = "photos/profile-photos";

        String key = String.format("%s/%s-%s", folder, UUID.randomUUID(), fileName);
        return new ProcessedPhotoDetails(fileName, contentType, ext, key);
    }
}
