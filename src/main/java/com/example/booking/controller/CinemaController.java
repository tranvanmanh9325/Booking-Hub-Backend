package com.example.booking.controller;

import com.example.booking.dto.CinemaDTO;
import com.example.booking.service.MovieService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Min;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cinemas")
@CrossOrigin(origins = "*")
@Validated
public class CinemaController {

    private final MovieService movieService;

    public CinemaController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<List<CinemaDTO>> getAllCinemas() {
        return ResponseEntity.ok(movieService.getAllCinemas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaDTO> getCinemaById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(movieService.getCinemaById(id));
    }
}