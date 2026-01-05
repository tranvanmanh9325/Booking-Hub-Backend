package com.example.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CinemaDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String facilities;
    private String phoneNumber;
}