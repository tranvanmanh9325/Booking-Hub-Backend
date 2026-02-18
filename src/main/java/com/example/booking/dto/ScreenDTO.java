package com.example.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenDTO {
    private Long id;
    private String name;
    private Integer capacity;
    private String screenType;
    private Long cinemaId;
    private String cinemaName;
}
