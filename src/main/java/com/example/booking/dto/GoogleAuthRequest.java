package com.example.booking.dto;

import lombok.Data;

@Data
public class GoogleAuthRequest {
    private String email;
    private String name;
    private String picture;
    private String googleId;
}
