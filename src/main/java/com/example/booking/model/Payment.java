package com.example.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;
    
    @Column(name = "booking_type", nullable = false)
    private String bookingType; // MOVIE, HOTEL, RESTAURANT, PARK
    
    @Column(name = "amount", nullable = false)
    private Double amount;
    
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // VISA, MASTERCARD, MOMO, ZALOPAY, VNPAY
    
    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    
    @Column(nullable = false)
    private String status; // PENDING, SUCCESS, FAILED, REFUNDED
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (transactionId == null) {
            transactionId = "TXN" + System.currentTimeMillis();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

