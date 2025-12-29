package com.mimaja.job_finder_app.feature.user.update.controller;

import com.mimaja.job_finder_app.feature.user.update.service.UserUpdateService;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateEmailRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdatePhoneNumberRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.requestDto.UpdateUserDataRequestDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateEmailResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdatePhoneNumberResponseDto;
import com.mimaja.job_finder_app.feature.user.update.shared.responseDto.UpdateUserDataResponseDto;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import com.mimaja.job_finder_app.shared.record.JwtPrincipal;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user/update")
@RequiredArgsConstructor
public class UserUpdateController {
    private final UserUpdateService userUpdateService;

    @PatchMapping("/email")
    public ResponseDto<UpdateEmailResponseDto> updateEmail(
            @RequestBody @Valid UpdateEmailRequestDto reqBody,
            @AuthenticationPrincipal JwtPrincipal principal) {
        UpdateEmailResponseDto response = userUpdateService.updateEmail(reqBody, principal);

        return new ResponseDto<>(SuccessCode.RESOURCE_UPDATED, "Email updated", response);
    }

    @PatchMapping("/phone-number")
    public ResponseDto<UpdatePhoneNumberResponseDto> updatePhoneNumber(
            @RequestBody @Valid UpdatePhoneNumberRequestDto reqData,
            @AuthenticationPrincipal JwtPrincipal principal) {
        UpdatePhoneNumberResponseDto response =
                userUpdateService.updatePhoneNumber(reqData, principal);

        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED, "Phone number successfully updated", response);
    }

    @PatchMapping(path = "/user-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<UpdateUserDataResponseDto> updateUserdata(
            @AuthenticationPrincipal JwtPrincipal principal,
            @RequestParam("profilePhoto") Optional<MultipartFile> profilePhoto,
            @RequestParam("newUsername") String newUsername,
            @RequestParam("newFirstName") String newFirstName,
            @RequestParam("newLastName") String newLastName,
            @RequestParam("newProfileDescription") String newProfileDescription,
            @RequestParam("password") String password) {
        UpdateUserDataRequestDto reqData =
                new UpdateUserDataRequestDto(
                        newUsername, newFirstName, newLastName, newProfileDescription, password);
        UpdateUserDataResponseDto response =
                userUpdateService.updateUserdata(profilePhoto, reqData, principal);

        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED, "User data successfully updated", response);
    }
}
