package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String avatarUrl;
    private String partnerType;

    public AuthResponse() {
    }

    public AuthResponse(String token, String refreshToken, String type, Long userId, String email, String fullName,
            String phone, String role, String avatarUrl) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.type = type;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.avatarUrl = avatarUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AuthResponse that = (AuthResponse) o;
        return java.util.Objects.equals(token, that.token) &&
                java.util.Objects.equals(type, that.type) &&
                java.util.Objects.equals(userId, that.userId) &&
                java.util.Objects.equals(email, that.email) &&
                java.util.Objects.equals(fullName, that.fullName) &&
                java.util.Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(token, type, userId, email, fullName, phone);
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", type='" + type + '\'' +
                ", userId=" + userId +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", partnerType='" + partnerType + '\'' +
                '}';
    }
}