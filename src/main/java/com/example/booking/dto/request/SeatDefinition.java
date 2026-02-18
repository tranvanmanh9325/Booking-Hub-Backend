package com.example.booking.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDefinition {
    private String row;
    private int number;
    private String type; // STANDARD, VIP, COUPLE
}