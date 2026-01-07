package com.example.booking.dto;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String fullName;

    public AuthResponse() {
    }

    public AuthResponse(String token, String type, Long userId, String email, String fullName) {
        this.token = token;
        this.type = type;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
                java.util.Objects.equals(fullName, that.fullName);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(token, type, userId, email, fullName);
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", type='" + type + '\'' +
                ", userId=" + userId +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}