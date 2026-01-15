package com.example.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.booking.validation.PhoneNumber;
import com.example.booking.validation.StrongPassword;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @StrongPassword
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @PhoneNumber(message = "Invalid phone number format. Must be 10 digits starting with 0.")
    private String phone;

    public RegisterRequest() {
    }

    public RegisterRequest(String email, String password, String fullName, String phone) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RegisterRequest that = (RegisterRequest) o;
        return java.util.Objects.equals(email, that.email) &&
                java.util.Objects.equals(password, that.password) &&
                java.util.Objects.equals(fullName, that.fullName) &&
                java.util.Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(email, password, fullName, phone);
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}