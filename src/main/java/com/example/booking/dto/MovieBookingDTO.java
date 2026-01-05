package com.example.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieBookingDTO {
    private Long id;
    private Long userId;
    private Long showtimeId;
    private String movieTitle;
    private String cinemaName;
    private String screenName;
    private LocalDateTime showtimeStart;
    private LocalDateTime bookingDate;
    private String status;
    private Double totalPrice;
    private List<SeatDTO> seats;
}