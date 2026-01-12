package com.example.booking.controller;

// DTO Import
import com.example.booking.dto.CinemaDTO;
import com.example.booking.service.SearchService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Min;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Controller quản lý thông tin rạp chiếu phim.
 * Cung cấp API để tra cứu danh sách và chi tiết rạp.
 */
@RestController
@RequestMapping("/api/v1/cinemas")
@CrossOrigin(origins = "*")
@Validated
@Tag(name = "Cinemas", description = "Cinema information APIs")
public class CinemaController {

    private final SearchService searchService;

    public CinemaController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Lấy danh sách tất cả rạp chiếu phim.
     * 
     * @return ResponseEntity chứa danh sách CinemaDTO
     */
    @Operation(summary = "Get all cinemas", description = "Retrieves a list of all cinemas.")
    @GetMapping
    public ResponseEntity<List<CinemaDTO>> getAllCinemas() {
        return ResponseEntity.ok(searchService.getAllCinemas());
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
        return ResponseEntity.ok(searchService.getCinemaById(id));
    }
}