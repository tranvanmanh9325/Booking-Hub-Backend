package com.example.booking.mapper;

import com.example.booking.dto.AuthResponse;
import com.example.booking.dto.RegisterRequest;
import com.example.booking.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "password", ignore = true) // Handled by PasswordEncoder in service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "resetPasswordToken", ignore = true)
    @Mapping(target = "resetPasswordTokenExpiry", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "partnerType", ignore = true)
    User toUser(RegisterRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "token", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "type", source = "tokenType")
    @Mapping(target = "role", source = "user.role")
    @Mapping(target = "avatarUrl", source = "user.avatarUrl")
    @Mapping(target = "partnerType", source = "user.partnerType")
    AuthResponse toAuthResponse(User user, String accessToken, String refreshToken, String tokenType);
}