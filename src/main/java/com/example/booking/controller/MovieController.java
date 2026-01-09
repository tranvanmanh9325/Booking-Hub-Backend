package com.example.booking.controller;

import com.example.booking.dto.BookMovieRequest;
import com.example.booking.dto.MovieBookingDTO;
import com.example.booking.dto.MovieDTO;
import com.example.booking.dto.SeatDTO;
import com.example.booking.dto.ShowtimeDTO;
import com.example.booking.model.User;
import com.example.booking.service.BookingService;
import com.example.booking.service.SearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin(origins = "*")
@Validated
public class MovieController {

    private final SearchService searchService;
    private final BookingService bookingService;

    public MovieController(SearchService searchService, BookingService bookingService) {
        this.searchService = searchService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<Page<MovieDTO>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(searchService.getAllMovies(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(searchService.getMovieById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieDTO>> searchMovies(@RequestParam String q) {
        return ResponseEntity.ok(searchService.searchMovies(q));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieDTO>> getMoviesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(searchService.getMoviesByGenre(genre));
    }

    @GetMapping("/now-showing")
    public ResponseEntity<List<MovieDTO>> getNowShowing() {
        return ResponseEntity.ok(searchService.getNowShowing());
    }

    @GetMapping("/{movieId}/showtimes")
    public ResponseEntity<List<ShowtimeDTO>> getShowtimesByMovie(@PathVariable @Min(1) Long movieId) {
        return ResponseEntity.ok(searchService.getShowtimesByMovie(movieId));
    }

    @GetMapping("/showtimes/{showtimeId}/seats")
    public ResponseEntity<List<SeatDTO>> getSeatsByShowtime(
            @PathVariable @Min(1) Long showtimeId,
            @RequestParam @Min(1) Long screenId) {
        return ResponseEntity.ok(searchService.getSeatsByScreen(screenId, showtimeId));
    }

    @PostMapping("/book")
    public ResponseEntity<MovieBookingDTO> bookMovie(
            @Valid @RequestBody BookMovieRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.bookMovie(user.getId(), request));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<MovieBookingDTO>> getUserBookings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.getUserMovieBookings(user.getId()));
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<MovieBookingDTO> getBookingById(
            @PathVariable @Min(1) Long bookingId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.getMovieBookingById(bookingId, user.getId()));
    }

    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<MovieBookingDTO> cancelBooking(
            @PathVariable @Min(1) Long bookingId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.cancelMovieBooking(bookingId, user.getId()));
    }
}