package com.example.booking.dto;

public class HotelRatingDTO {
    private Long hotelId;
    private Double averageRating;

    public HotelRatingDTO(Long hotelId, Double averageRating) {
        this.hotelId = hotelId;
        this.averageRating = averageRating;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
}
