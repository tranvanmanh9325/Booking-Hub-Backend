package com.example.booking.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.example.booking.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HotelBookingDTO {
    private Long id;
    private Long userId;
    private Long hotelId;
    private String hotelName;
    private Long roomId;
    private String roomType;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guests;
    private Double totalPrice;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public HotelBookingDTO() {
    }

    public HotelBookingDTO(Long id, Long userId, Long hotelId, String hotelName, Long roomId, String roomType,
            LocalDate checkIn, LocalDate checkOut, Integer guests, Double totalPrice, BookingStatus status,
            LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.roomId = roomId;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HotelBookingDTO that = (HotelBookingDTO) o;
        return java.util.Objects.equals(id, that.id) &&
                java.util.Objects.equals(userId, that.userId) &&
                java.util.Objects.equals(hotelId, that.hotelId) &&
                java.util.Objects.equals(hotelName, that.hotelName) &&
                java.util.Objects.equals(roomId, that.roomId) &&
                java.util.Objects.equals(roomType, that.roomType) &&
                java.util.Objects.equals(checkIn, that.checkIn) &&
                java.util.Objects.equals(checkOut, that.checkOut) &&
                java.util.Objects.equals(guests, that.guests) &&
                java.util.Objects.equals(totalPrice, that.totalPrice) &&
                java.util.Objects.equals(status, that.status) &&
                java.util.Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, userId, hotelId, hotelName, roomId, roomType, checkIn, checkOut, guests,
                totalPrice, status, createdAt);
    }

    @Override
    public String toString() {
        return "HotelBookingDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", hotelId=" + hotelId +
                ", hotelName='" + hotelName + '\'' +
                ", roomId=" + roomId +
                ", roomType='" + roomType + '\'' +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", guests=" + guests +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}