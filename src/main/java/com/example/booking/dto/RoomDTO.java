package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RoomDTO {
    private Long id;
    private Long hotelId;
    private String roomType;
    private Integer maxGuests;
    private Double pricePerNight;
    private String amenities;
    private String roomNumber;
    private List<String> imageUrls;
    private Boolean isAvailable;

    public RoomDTO() {
    }

    public RoomDTO(Long id, Long hotelId, String roomType, Integer maxGuests, Double pricePerNight, String amenities,
            String roomNumber, List<String> imageUrls, Boolean isAvailable) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomType = roomType;
        this.maxGuests = maxGuests;
        this.pricePerNight = pricePerNight;
        this.amenities = amenities;
        this.roomNumber = roomNumber;
        this.imageUrls = imageUrls;
        this.isAvailable = isAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoomDTO roomDTO = (RoomDTO) o;
        return java.util.Objects.equals(id, roomDTO.id) &&
                java.util.Objects.equals(hotelId, roomDTO.hotelId) &&
                java.util.Objects.equals(roomType, roomDTO.roomType) &&
                java.util.Objects.equals(maxGuests, roomDTO.maxGuests) &&
                java.util.Objects.equals(pricePerNight, roomDTO.pricePerNight) &&
                java.util.Objects.equals(amenities, roomDTO.amenities) &&
                java.util.Objects.equals(roomNumber, roomDTO.roomNumber) &&
                java.util.Objects.equals(imageUrls, roomDTO.imageUrls) &&
                java.util.Objects.equals(isAvailable, roomDTO.isAvailable);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, hotelId, roomType, maxGuests, pricePerNight, amenities, roomNumber, imageUrls,
                isAvailable);
    }

    @Override
    public String toString() {
        return "RoomDTO{" +
                "id=" + id +
                ", hotelId=" + hotelId +
                ", roomType='" + roomType + '\'' +
                ", maxGuests=" + maxGuests +
                ", pricePerNight=" + pricePerNight +
                ", amenities='" + amenities + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", imageUrls=" + imageUrls +
                ", isAvailable=" + isAvailable +
                '}';
    }
}