package com.example.booking.dto.request;

import lombok.Data;

@Data
public class SeatGenerationRequest {
    private int rowCount;
    private int colCount;
    private java.util.List<SeatDefinition> seats;
}