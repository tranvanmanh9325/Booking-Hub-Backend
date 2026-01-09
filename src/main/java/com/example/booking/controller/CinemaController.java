package com.example.booking.controller;

import com.example.booking.dto.CinemaDTO;
import com.example.booking.service.SearchService;

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

    private final SearchService searchService;

    public CinemaController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<List<CinemaDTO>> getAllCinemas() {
        return ResponseEntity.ok(searchService.getAllCinemas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CinemaDTO> getCinemaById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(searchService.getCinemaById(id));
    }
}