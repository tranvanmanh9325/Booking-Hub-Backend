package com.example.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Future;

import java.time.LocalDate;

import com.example.booking.validation.DateRange;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@DateRange(start = "checkIn", end = "checkOut")
public class BookHotelRequest {

    @NotNull(message = "Hotel ID is required")
    @jakarta.validation.constraints.Min(value = 1, message = "Hotel ID must be positive")
    private Long hotelId;

    @NotNull(message = "Room ID is required")
    @jakarta.validation.constraints.Min(value = 1, message = "Room ID must be positive")
    private Long roomId;

    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOut;

    @NotNull(message = "Number of guests is required")
    @Positive(message = "Number of guests must be positive")
    private Integer guests;

    public BookHotelRequest() {
    }

    public BookHotelRequest(Long hotelId, Long roomId, LocalDate checkIn, LocalDate checkOut, Integer guests) {
        this.hotelId = hotelId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BookHotelRequest that = (BookHotelRequest) o;
        return java.util.Objects.equals(hotelId, that.hotelId) &&
                java.util.Objects.equals(roomId, that.roomId) &&
                java.util.Objects.equals(checkIn, that.checkIn) &&
                java.util.Objects.equals(checkOut, that.checkOut) &&
                java.util.Objects.equals(guests, that.guests);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(hotelId, roomId, checkIn, checkOut, guests);
    }

    @Override
    public String toString() {
        return "BookHotelRequest{" +
                "hotelId=" + hotelId +
                ", roomId=" + roomId +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", guests=" + guests +
                '}';
    }
}