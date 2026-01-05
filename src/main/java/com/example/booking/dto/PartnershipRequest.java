package com.example.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PartnershipRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone is required")
    private String phone;
    
    private String company;
    
    @NotBlank(message = "Partnership type is required")
    private String partnershipType;
    
    private String message;
}