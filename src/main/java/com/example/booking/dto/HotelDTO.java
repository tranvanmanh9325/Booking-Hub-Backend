package com.example.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Integer starRating;
    private String description;
    private String facilities;
    private String phoneNumber;
    private String email;
    private Double averageRating;
}