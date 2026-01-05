package com.example.booking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookMovieRequest {
    
    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;
    
    @NotEmpty(message = "At least one seat must be selected")
    private List<Long> seatIds;
}