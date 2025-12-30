package com.example.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long bookingId;
    private String bookingType;
    private Double amount;
    private String paymentMethod;
    private String transactionId;
    private String status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}

