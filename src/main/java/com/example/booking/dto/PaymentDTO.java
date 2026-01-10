package com.example.booking.dto;

import java.time.LocalDateTime;
import com.example.booking.enums.BookingType;
import com.example.booking.enums.PaymentMethod;
import com.example.booking.enums.PaymentStatus;

public class PaymentDTO {
    private Long id;
    private Long bookingId;
    private BookingType bookingType;
    private Double amount;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public PaymentDTO() {
    }

    public PaymentDTO(Long id, Long bookingId, BookingType bookingType, Double amount, PaymentMethod paymentMethod,
            String transactionId, PaymentStatus status, LocalDateTime paidAt, LocalDateTime createdAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.bookingType = bookingType;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PaymentDTO that = (PaymentDTO) o;
        return java.util.Objects.equals(id, that.id) &&
                java.util.Objects.equals(bookingId, that.bookingId) &&
                java.util.Objects.equals(bookingType, that.bookingType) &&
                java.util.Objects.equals(amount, that.amount) &&
                java.util.Objects.equals(paymentMethod, that.paymentMethod) &&
                java.util.Objects.equals(transactionId, that.transactionId) &&
                java.util.Objects.equals(status, that.status) &&
                java.util.Objects.equals(paidAt, that.paidAt) &&
                java.util.Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, bookingId, bookingType, amount, paymentMethod, transactionId, status, paidAt,
                createdAt);
    }

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "id=" + id +
                ", bookingId=" + bookingId +
                ", bookingType=" + bookingType +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", transactionId='" + transactionId + '\'' +
                ", status=" + status +
                ", paidAt=" + paidAt +
                ", createdAt=" + createdAt +
                '}';
    }
}