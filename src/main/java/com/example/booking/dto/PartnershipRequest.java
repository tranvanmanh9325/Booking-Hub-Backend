package com.example.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.example.booking.validation.PhoneNumber;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PartnershipRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    @PhoneNumber(message = "Invalid phone number format. Must be 10 digits starting with 0.")
    private String phone;

    private String company;

    @NotBlank(message = "Partnership type is required")
    private String partnershipType;

    private String message;

    public PartnershipRequest() {
    }

    public PartnershipRequest(String name, String email, String phone, String company, String partnershipType,
            String message) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.partnershipType = partnershipType;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PartnershipRequest that = (PartnershipRequest) o;
        return java.util.Objects.equals(name, that.name) &&
                java.util.Objects.equals(email, that.email) &&
                java.util.Objects.equals(phone, that.phone) &&
                java.util.Objects.equals(company, that.company) &&
                java.util.Objects.equals(partnershipType, that.partnershipType) &&
                java.util.Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, email, phone, company, partnershipType, message);
    }

    @Override
    public String toString() {
        return "PartnershipRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", company='" + company + '\'' +
                ", partnershipType='" + partnershipType + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}