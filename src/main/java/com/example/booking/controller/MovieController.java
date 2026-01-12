package com.example.booking.controller;

// DTO Imports
// DTO Imports
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

/**
 * Controller quản lý phim và đặt vé.
 * Cung cấp API tìm kiếm phim, xem lịch chiếu, chọn ghế và đặt vé.
 */
@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin(origins = "*")
@Validated
@Tag(name = "Movies", description = "Movie management and booking APIs")
public class MovieController {

    private final SearchService searchService;
    private final BookingService bookingService;

    public MovieController(SearchService searchService, BookingService bookingService) {
        this.searchService = searchService;
        this.bookingService = bookingService;
    }

    /**
     * Lấy danh sách tất cả phim (có phân trang).
     * 
     * @param page Số trang
     * @param size Số lượng item mỗi trang
     * @return ResponseEntity chứa Page<MovieDTO>
     */
    @Operation(summary = "Get all movies", description = "Retrieves a paginated list of all movies.")
    @GetMapping
    public ResponseEntity<Page<MovieDTO>> getAllMovies(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(searchService.getAllMovies(page, size));
    }

    /**
     * Lấy thông tin chi tiết phim theo ID.
     * 
     * @param id ID của phim
     * @return ResponseEntity chứa MovieDTO
     */
    @Operation(summary = "Get movie by ID", description = "Retrieves detailed information about a specific movie.")
    @ApiResponse(responseCode = "200", description = "Movie found", content = @Content(schema = @Schema(implementation = MovieDTO.class)))
    @ApiResponse(responseCode = "404", description = "Movie not found")
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(
            @Parameter(description = "ID of the movie") @PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(searchService.getMovieById(id));
    }

    /**
     * Tìm kiếm phim theo từ khóa.
     * 
     * @param q Từ khóa tìm kiếm
     * @return ResponseEntity chứa List<MovieDTO>
     */
    @Operation(summary = "Search movies", description = "Search movies by keyword.")
    @GetMapping("/search")
    public ResponseEntity<List<MovieDTO>> searchMovies(
            @Parameter(description = "Search keyword") @RequestParam String q) {
        return ResponseEntity.ok(searchService.searchMovies(q));
    }

    /**
     * Lấy danh sách phim theo thể loại.
     * 
     * @param genre Tên thể loại
     * @return ResponseEntity chứa List<MovieDTO>
     */
    @Operation(summary = "Get movies by genre", description = "Retrieves a list of movies in a specific genre.")
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieDTO>> getMoviesByGenre(
            @Parameter(description = "Genre name") @PathVariable String genre) {
        return ResponseEntity.ok(searchService.getMoviesByGenre(genre));
    }

    /**
     * Lấy danh sách phim đang chiếu.
     * 
     * @return ResponseEntity chứa List<MovieDTO>
     */
    @Operation(summary = "Get now showing movies", description = "Retrieves a list of movies currently showing.")
    @GetMapping("/now-showing")
    public ResponseEntity<List<MovieDTO>> getNowShowing() {
        return ResponseEntity.ok(searchService.getNowShowing());
    }

    /**
     * Lấy lịch chiếu của một phim.
     * 
     * @param movieId ID của phim
     * @return ResponseEntity chứa List<ShowtimeDTO>
     */
    @Operation(summary = "Get showtimes by movie", description = "Retrieves scheduled showtimes for a specific movie.")
    @GetMapping("/{movieId}/showtimes")
    public ResponseEntity<List<ShowtimeDTO>> getShowtimesByMovie(
            @Parameter(description = "ID of the movie") @PathVariable @Min(1) Long movieId) {
        return ResponseEntity.ok(searchService.getShowtimesByMovie(movieId));
    }

    /**
     * Lấy danh sách ghế của một suất chiếu.
     * 
     * @param showtimeId ID suất chiếu
     * @param screenId   ID phòng chiếu
     * @return ResponseEntity chứa List<SeatDTO>
     */
    @Operation(summary = "Get seats by showtime", description = "Retrieves available seats for a specific showtime.")
    @GetMapping("/showtimes/{showtimeId}/seats")
    public ResponseEntity<List<SeatDTO>> getSeatsByShowtime(
            @Parameter(description = "ID of the showtime") @PathVariable @Min(1) Long showtimeId,
            @Parameter(description = "ID of the screen") @RequestParam @Min(1) Long screenId) {
        return ResponseEntity.ok(searchService.getSeatsByScreen(screenId, showtimeId));
    }

    /**
     * Đặt vé xem phim.
     * 
     * @param request        Thông tin đặt vé
     * @param authentication Thông tin xác thực
     * @return ResponseEntity chứa MovieBookingDTO
     */
    @Operation(summary = "Book a movie", description = "Creates a new movie booking.")
    @SecurityRequirement(name = "bearerAuth")
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

    /**
     * Lấy lịch sử đặt vé của người dùng.
     * 
     * @param authentication Thông tin xác thực
     * @return ResponseEntity chứa List<MovieBookingDTO>
     */
    @Operation(summary = "Get user bookings", description = "Retrieves all movie bookings for the authenticated user.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/bookings")
    public ResponseEntity<List<MovieBookingDTO>> getUserBookings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.getUserMovieBookings(user.getId()));
    }

    /**
     * Lấy chi tiết vé đặt theo ID.
     * 
     * @param bookingId      ID booking
     * @param authentication Thông tin xác thực
     * @return ResponseEntity chứa MovieBookingDTO
     */
    @Operation(summary = "Get booking by ID", description = "Retrieves a specific movie booking by ID.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<MovieBookingDTO> getBookingById(
            @Parameter(description = "ID of the booking") @PathVariable @Min(1) Long bookingId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.getMovieBookingById(bookingId, user.getId()));
    }

    /**
     * Hủy đặt vé.
     * 
     * @param bookingId      ID booking
     * @param authentication Thông tin xác thực
     * @return ResponseEntity chứa MovieBookingDTO đã hủy
     */
    @Operation(summary = "Cancel booking", description = "Cancels a specific movie booking.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<MovieBookingDTO> cancelBooking(
            @Parameter(description = "ID of the booking") @PathVariable @Min(1) Long bookingId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.cancelMovieBooking(bookingId, user.getId()));
    }
}