package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HotelRatingDTO {
    private Long hotelId;
    private Double averageRating;

    public HotelRatingDTO(Long hotelId, Double averageRating) {
        this.hotelId = hotelId;
        this.averageRating = averageRating;
    }

}
