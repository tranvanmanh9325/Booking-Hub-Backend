package com.example.booking.mapper;

import com.example.booking.dto.AuthResponse;
import com.example.booking.dto.RegisterRequest;
import com.example.booking.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true) // Handled by PasswordEncoder in service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "resetPasswordToken", ignore = true)
    @Mapping(target = "resetPasswordTokenExpiry", ignore = true)
    User toUser(RegisterRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "refreshToken", source = "refreshToken")
    @Mapping(target = "type", source = "type")
    AuthResponse toAuthResponse(User user, String token, String refreshToken, String type);
}
