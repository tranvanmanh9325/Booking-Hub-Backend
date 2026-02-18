package com.example.booking.controller;

import com.example.booking.dto.ShowtimeDTO;
import com.example.booking.service.ShowtimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/showtimes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Showtimes", description = "Showtime management APIs")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @Operation(summary = "Get showtimes by content")
    @GetMapping("/content/{contentId}")
    public ResponseEntity<List<ShowtimeDTO>> getShowtimesByContent(@PathVariable Long contentId) {
        return ResponseEntity.ok(showtimeService.getShowtimesByContent(contentId));
    }

    @Operation(summary = "Create a showtime")
    @PostMapping
    public ResponseEntity<ShowtimeDTO> createShowtime(@RequestBody ShowtimeDTO showtimeDTO) {
        return ResponseEntity.ok(showtimeService.createShowtime(showtimeDTO));
    }

    @Operation(summary = "Update a showtime")
    @PutMapping("/{id}")
    public ResponseEntity<ShowtimeDTO> updateShowtime(@PathVariable Long id, @RequestBody ShowtimeDTO showtimeDTO) {
        return ResponseEntity.ok(showtimeService.updateShowtime(id, showtimeDTO));
    }

    @Operation(summary = "Delete a showtime")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.ok().build();
    }
}