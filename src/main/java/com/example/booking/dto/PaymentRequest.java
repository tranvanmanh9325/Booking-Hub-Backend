package com.example.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.example.booking.enums.BookingType;
import com.example.booking.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Booking type is required")
    private BookingType bookingType;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    public PaymentRequest() {
    }

    public PaymentRequest(Long bookingId, BookingType bookingType, Double amount, PaymentMethod paymentMethod) {
        this.bookingId = bookingId;
        this.bookingType = bookingType;
        this.amount = amount;
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
                ", bookingType=" + bookingType +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                '}';
    }
}