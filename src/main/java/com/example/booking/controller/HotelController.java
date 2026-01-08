package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.model.User;
import com.example.booking.service.HotelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@CrossOrigin(origins = "*")
@Validated
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public ResponseEntity<Page<HotelDTO>> getAllHotels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(hotelService.getAllHotels(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<HotelDTO>> searchHotels(@RequestParam String q) {
        return ResponseEntity.ok(hotelService.searchHotels(q));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<HotelDTO>> getHotelsByCity(@PathVariable String city) {
        return ResponseEntity.ok(hotelService.getHotelsByCity(city));
    }

    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<RoomDTO>> getRoomsByHotel(
            @PathVariable @Min(1) Long hotelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false, defaultValue = "1") Integer guests) {

        if (checkIn == null) {
            checkIn = LocalDate.now();
        }
        if (checkOut == null) {
            checkOut = checkIn.plusDays(1);
        }

        return ResponseEntity.ok(hotelService.getRoomsByHotel(hotelId, checkIn, checkOut, guests));
    }

    @PostMapping("/book")
    public ResponseEntity<HotelBookingDTO> bookHotel(
            @Valid @RequestBody BookHotelRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(hotelService.bookHotel(user.getId(), request));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<HotelBookingDTO>> getUserBookings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(hotelService.getUserBookings(user.getId()));
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<HotelBookingDTO> getBookingById(
            @PathVariable @Min(1) Long bookingId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(hotelService.getBookingById(bookingId, user.getId()));
    }

    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<HotelBookingDTO> cancelBooking(
            @PathVariable @Min(1) Long bookingId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(hotelService.cancelBooking(bookingId, user.getId()));
    }
}