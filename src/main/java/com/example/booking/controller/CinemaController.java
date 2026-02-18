package com.example.booking.controller;

import com.example.booking.dto.CinemaDTO;
import com.example.booking.dto.ScreenDTO;
import com.example.booking.service.CinemaService;

import com.example.booking.service.ScreenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý thông tin rạp chiếu phim.
 * Cung cấp API để tra cứu và quản lý danh sách rạp.
 */
@RestController
@RequestMapping("/api/v1/cinemas")
@CrossOrigin(origins = "*")
@Validated
@Tag(name = "Cinemas", description = "Cinema information and management APIs")
public class CinemaController {

    private final CinemaService cinemaService;
    private final ScreenService screenService;

    public CinemaController(CinemaService cinemaService, ScreenService screenService) {
        this.cinemaService = cinemaService;
        this.screenService = screenService;
    }

    /**
     * Lấy danh sách tất cả rạp chiếu phim.
     * 
     * @return ResponseEntity chứa danh sách CinemaDTO
     */
    @Operation(summary = "Get all cinemas", description = "Retrieves a list of all cinemas.")
    @GetMapping
    public ResponseEntity<List<CinemaDTO>> getAllCinemas() {
        // Delegating to CinemaService for consistency, though SearchService also has
        // this.
        return ResponseEntity.ok(cinemaService.getAllCinemas());
    }

    /**
     * Lấy thông tin chi tiết của một rạp theo ID.
     * 
     * @param id ID của rạp
     * @return ResponseEntity chứa CinemaDTO
     */
    @Operation(summary = "Get cinema by ID", description = "Retrieves details of a specific cinema.")
    @ApiResponse(responseCode = "200", description = "Cinema found", content = @Content(schema = @Schema(implementation = CinemaDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cinema not found")
    @GetMapping("/{id}")
    public ResponseEntity<CinemaDTO> getCinemaById(
            @Parameter(description = "ID of the cinema") @PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(cinemaService.getCinemaById(id));
    }

    @Operation(summary = "Get screens by cinema ID", description = "Retrieves a list of screens for a specific cinema.")
    @GetMapping("/{id}/screens")
    public ResponseEntity<List<ScreenDTO>> getScreensByCinema(
            @Parameter(description = "ID of the cinema") @PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(screenService.getScreensByCinema(id));
    }

    // --- CRUD Operations ---

    @Operation(summary = "Create a new cinema", description = "Creates a new cinema.")
    @PostMapping
    public ResponseEntity<CinemaDTO> createCinema(@Valid @RequestBody CinemaDTO cinemaDTO) {
        return ResponseEntity.ok(cinemaService.createCinema(cinemaDTO));
    }

    @Operation(summary = "Update a cinema", description = "Updates an existing cinema.")
    @PutMapping("/{id}")
    public ResponseEntity<CinemaDTO> updateCinema(
            @Parameter(description = "ID of the cinema") @PathVariable @Min(1) Long id,
            @Valid @RequestBody CinemaDTO cinemaDTO) {
        return ResponseEntity.ok(cinemaService.updateCinema(id, cinemaDTO));
    }

    @Operation(summary = "Delete a cinema", description = "Deletes a cinema.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCinema(
            @Parameter(description = "ID of the cinema") @PathVariable @Min(1) Long id) {
        cinemaService.deleteCinema(id);
        return ResponseEntity.ok().build();
    }
}