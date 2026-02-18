package com.example.booking.controller;

import com.example.booking.dto.ScreenDTO;
import com.example.booking.dto.SeatDTO;
import com.example.booking.dto.request.SeatGenerationRequest;
import com.example.booking.service.ScreenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/screens")
@CrossOrigin(origins = "*")
@Validated
@Tag(name = "Screens", description = "Screen management APIs")
public class ScreenController {

    private final ScreenService screenService;

    public ScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    @Operation(summary = "Get screen by ID", description = "Retrieves details of a specific screen.") // Added missing
                                                                                                      // endpoint
    @GetMapping("/{id}")
    public ResponseEntity<ScreenDTO> getScreenById(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(screenService.getScreenById(id));
    }

    @Operation(summary = "Create a new screen", description = "Creates a new screen.")
    @PostMapping
    public ResponseEntity<ScreenDTO> createScreen(@Valid @RequestBody ScreenDTO screenDTO) {
        return ResponseEntity.ok(screenService.createScreen(screenDTO));
    }

    @Operation(summary = "Update a screen", description = "Updates an existing screen.")
    @PutMapping("/{id}")
    public ResponseEntity<ScreenDTO> updateScreen(
            @Parameter(description = "ID of the screen") @PathVariable @Min(1) Long id,
            @Valid @RequestBody ScreenDTO screenDTO) {
        return ResponseEntity.ok(screenService.updateScreen(id, screenDTO));
    }

    @Operation(summary = "Delete a screen", description = "Deletes a screen.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScreen(
            @Parameter(description = "ID of the screen") @PathVariable @Min(1) Long id) {
        screenService.deleteScreen(id);
        return ResponseEntity.ok().build();
    }

    // Existing methods (might need to be moved to ScreenService fully if not
    // already)
    // Looking at previous file content, generateSeats and getSeats were there.
    // ScreenService needs to support them. I'll assume generateSeats and
    // getSeatsByScreenId exist in ScreenService as per previous read,
    // although my create of ScreenService in previous step didn't include them
    // explicitly.
    // Wait, the previous read of ScreenController showed generateSeats and getSeats
    // calling screenService.
    // BUT the ScreenService I created in step 92 DOES NOT HAVE them.
    // I need to add them to ScreenService.

    @Operation(summary = "Generate seats for a screen", description = "Deletes existing seats and generates a new grid.")
    @PostMapping("/{id}/seats")
    public ResponseEntity<Void> generateSeats(
            @PathVariable Long id,
            @RequestBody SeatGenerationRequest request) {
        screenService.generateSeats(id, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get seats for a screen", description = "Retrieves all seats for a specific screen.")
    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatDTO>> getSeats(@PathVariable Long id) {
        return ResponseEntity.ok(screenService.getSeatsByScreenId(id));
    }
}