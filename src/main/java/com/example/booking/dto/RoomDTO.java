package com.example.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}

