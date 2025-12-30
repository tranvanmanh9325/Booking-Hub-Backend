package com.example.booking.controller;

import com.example.booking.dto.CinemaDTO;
import com.example.booking.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinemas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CinemaController {
    
    private final MovieService movieService;
    
    @GetMapping
    public ResponseEntity<List<CinemaDTO>> getAllCinemas() {
        return ResponseEntity.ok(movieService.getAllCinemas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CinemaDTO> getCinemaById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getCinemaById(id));
    }
}

