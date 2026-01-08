package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.model.User;
import com.example.booking.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
@Validated
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<Page<MovieDTO>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(movieService.getAllMovies(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieDTO>> searchMovies(@RequestParam String q) {
        return ResponseEntity.ok(movieService.searchMovies(q));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieDTO>> getMoviesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(movieService.getMoviesByGenre(genre));
    }

    @GetMapping("/now-showing")
    public ResponseEntity<List<MovieDTO>> getNowShowing() {
        return ResponseEntity.ok(movieService.getNowShowing());
    }

    @GetMapping("/{movieId}/showtimes")
    public ResponseEntity<List<ShowtimeDTO>> getShowtimesByMovie(@PathVariable @Min(1) Long movieId) {
        return ResponseEntity.ok(movieService.getShowtimesByMovie(movieId));
    }

    @GetMapping("/showtimes/{showtimeId}/seats")
    public ResponseEntity<List<SeatDTO>> getSeatsByShowtime(
            @PathVariable @Min(1) Long showtimeId,
            @RequestParam @Min(1) Long screenId) {
        return ResponseEntity.ok(movieService.getSeatsByScreen(screenId, showtimeId));
    }

    @PostMapping("/book")
    public ResponseEntity<MovieBookingDTO> bookMovie(
            @Valid @RequestBody BookMovieRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(movieService.bookMovie(user.getId(), request));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<MovieBookingDTO>> getUserBookings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(movieService.getUserBookings(user.getId()));
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<MovieBookingDTO> getBookingById(
            @PathVariable @Min(1) Long bookingId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(movieService.getBookingById(bookingId, user.getId()));
    }

    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<MovieBookingDTO> cancelBooking(
            @PathVariable @Min(1) Long bookingId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(movieService.cancelBooking(bookingId, user.getId()));
    }
}