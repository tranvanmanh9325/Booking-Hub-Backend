package com.example.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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

    public PaymentRequest() {
    }

    public PaymentRequest(Long bookingId, String bookingType, Double amount, String paymentMethod) {
        this.bookingId = bookingId;
        this.bookingType = bookingType;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PaymentRequest that = (PaymentRequest) o;
        return java.util.Objects.equals(bookingId, that.bookingId) &&
                java.util.Objects.equals(bookingType, that.bookingType) &&
                java.util.Objects.equals(amount, that.amount) &&
                java.util.Objects.equals(paymentMethod, that.paymentMethod);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(bookingId, bookingType, amount, paymentMethod);
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "bookingId=" + bookingId +
                ", bookingType='" + bookingType + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}