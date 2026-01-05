package com.example.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    @NotBlank(message = "Booking type is required")
    private String bookingType; // MOVIE, HOTEL, RESTAURANT, PARK
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // VISA, MASTERCARD, MOMO, ZALOPAY, VNPAY
}