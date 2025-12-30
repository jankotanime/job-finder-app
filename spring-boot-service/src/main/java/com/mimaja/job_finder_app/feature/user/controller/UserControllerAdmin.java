package com.mimaja.job_finder_app.feature.user.controller;

import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelResponseDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelUpdateRequestDto;
import com.mimaja.job_finder_app.feature.user.service.UserServiceAdmin;
import com.mimaja.job_finder_app.shared.dto.ResponseDto;
import com.mimaja.job_finder_app.shared.enums.SuccessCode;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user")
public class UserControllerAdmin {
    private final UserServiceAdmin userServiceAdmin;
    private static final String ID = "/{userId}";

    @GetMapping
    public ResponseDto<Page<UserAdminPanelResponseDto>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return new ResponseDto<>(
                SuccessCode.RESPONSE_SUCCESSFUL,
                "Successfully fetched all users",
                userServiceAdmin.getAllUsers(pageable));
    }

    @PostMapping
    public ResponseEntity<ResponseDto<UserAdminPanelResponseDto>> createUser(
            @RequestBody @Valid UserAdminPanelCreateRequestDto userAdminPanelCreateRequestDto) {
        UserAdminPanelResponseDto dto = userServiceAdmin.createUser(userAdminPanelCreateRequestDto);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(dto.id())
                        .toUri();
        return ResponseEntity.created(location)
                .body(
                        new ResponseDto<>(
                                SuccessCode.RESOURCE_CREATED, "Successfully created user", dto));
    }

    @PutMapping(ID)
    public ResponseDto<UserAdminPanelResponseDto> updateUser(
            @RequestBody @Valid UserAdminPanelUpdateRequestDto userAdminPanelUpdateRequestDto,
            @PathVariable UUID userId) {
        UserAdminPanelResponseDto dto =
                userServiceAdmin.updateUser(userId, userAdminPanelUpdateRequestDto);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_UPDATED, "Successfully updated user with id: " + userId, dto);
    }

    @DeleteMapping(ID)
    public ResponseDto<Void> deleteUser(@PathVariable UUID userId) {
        userServiceAdmin.deleteUser(userId);
        return new ResponseDto<>(
                SuccessCode.RESOURCE_DELETED, "Successfully deleted user with id: " + userId, null);
    }
}
