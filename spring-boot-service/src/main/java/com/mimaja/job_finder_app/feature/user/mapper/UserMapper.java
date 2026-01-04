package com.mimaja.job_finder_app.feature.user.mapper;

import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelCreateRequestDto;
import com.mimaja.job_finder_app.feature.user.dto.UserAdminPanelResponseDto;
import com.mimaja.job_finder_app.feature.user.dto.UserInOfferResponseDto;
import com.mimaja.job_finder_app.feature.user.model.User;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "password", target = "passwordHash")
    User toEntity(UserAdminPanelCreateRequestDto userAdminPanelCreateRequestDto);

    UserInOfferResponseDto toUserInOfferResponseDto(User user);

    Set<UserInOfferResponseDto> toSetOfUserInOfferResponseDto(Set<User> users);

    UserAdminPanelResponseDto toUserAdminPanelResponseDto(User user);
}
