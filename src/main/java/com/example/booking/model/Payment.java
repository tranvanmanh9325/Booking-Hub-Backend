package com.example.booking.model;

import jakarta.persistence.*;
import com.example.booking.enums.BookingType;
import com.example.booking.enums.PaymentMethod;
import com.example.booking.enums.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", nullable = false)
    private BookingType bookingType;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

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

    public Payment() {
    }

    public Payment(Long id, Long bookingId, BookingType bookingType, Double amount, PaymentMethod paymentMethod,
            String transactionId, PaymentStatus status, LocalDateTime paidAt, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.bookingType = bookingType;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Payment payment = (Payment) o;
        return java.util.Objects.equals(id, payment.id) &&
                java.util.Objects.equals(bookingId, payment.bookingId) &&
                java.util.Objects.equals(bookingType, payment.bookingType) &&
                java.util.Objects.equals(amount, payment.amount) &&
                java.util.Objects.equals(paymentMethod, payment.paymentMethod) &&
                java.util.Objects.equals(transactionId, payment.transactionId) &&
                java.util.Objects.equals(status, payment.status) &&
                java.util.Objects.equals(paidAt, payment.paidAt) &&
                java.util.Objects.equals(createdAt, payment.createdAt) &&
                java.util.Objects.equals(updatedAt, payment.updatedAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, bookingId, bookingType, amount, paymentMethod, transactionId, status, paidAt,
                createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", bookingId=" + bookingId +
                ", bookingType=" + bookingType +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", transactionId='" + transactionId + '\'' +
                ", status=" + status +
                ", paidAt=" + paidAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}