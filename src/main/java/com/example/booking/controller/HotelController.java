package com.example.booking.controller;

import com.example.booking.dto.BookHotelRequest;
import com.example.booking.dto.HotelBookingDTO;
import com.example.booking.dto.HotelDTO;
import com.example.booking.dto.RoomDTO;
import com.example.booking.model.User;
import com.example.booking.service.BookingService;
import com.example.booking.service.SearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Controller quản lý khách sạn và đặt phòng.
 * Cung cấp API tìm kiếm, xem chi tiết và đặt phòng khách sạn.
 */
@RestController
@RequestMapping("/api/v1/hotels")
@CrossOrigin(origins = "*")
@Validated
@Tag(name = "Hotels", description = "Hotel management and booking APIs")
public class HotelController {

    private final SearchService searchService;
    private final BookingService bookingService;

    public HotelController(SearchService searchService, BookingService bookingService) {
        this.searchService = searchService;
        this.bookingService = bookingService;
    }

    /**
     * Lấy danh sách tất cả khách sạn (có phân trang).
     * 
     * @param page Số trang (bắt đầu từ 0)
     * @param size Số lượng item mỗi trang
     * @return ResponseEntity chứa Page<HotelDTO>
     */
    @Operation(summary = "Get all hotels", description = "Retrieves a paginated list of all hotels.")
    @GetMapping
    public ResponseEntity<Page<HotelDTO>> getAllHotels(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(searchService.getAllHotels(page, size));
    }

    /**
     * Lấy thông tin chi tiết khách sạn theo ID.
     * 
     * @param id ID của khách sạn
     * @return ResponseEntity chứa HotelDTO
     */
    @Operation(summary = "Get hotel by ID", description = "Retrieves detailed information about a specific hotel.")
    @ApiResponse(responseCode = "200", description = "Hotel found", content = @Content(schema = @Schema(implementation = HotelDTO.class)))
    @ApiResponse(responseCode = "404", description = "Hotel not found")
    @GetMapping("/{id}")
    public ResponseEntity<HotelDTO> getHotelById(
            @Parameter(description = "ID of the hotel") @PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(searchService.getHotelById(id));
    }

    /**
     * Tìm kiếm khách sạn theo từ khóa.
     * 
     * @param q Từ khóa tìm kiếm
     * @return ResponseEntity chứa List<HotelDTO>
     */
    @Operation(summary = "Search hotels", description = "Search hotels by keyword.")
    @GetMapping("/search")
    public ResponseEntity<List<HotelDTO>> searchHotels(
            @Parameter(description = "Search keyword") @RequestParam String q) {
        return ResponseEntity.ok(searchService.searchHotels(q));
    }

    /**
     * Lấy danh sách khách sạn theo thành phố.
     * 
     * @param city Tên thành phố
     * @return ResponseEntity chứa List<HotelDTO>
     */
    @Operation(summary = "Get hotels by city", description = "Retrieves a list of hotels in a specific city.")
    @GetMapping("/city/{city}")
    public ResponseEntity<List<HotelDTO>> getHotelsByCity(
            @Parameter(description = "Name of the city") @PathVariable String city) {
        return ResponseEntity.ok(searchService.getHotelsByCity(city));
    }

    /**
     * Lấy danh sách phòng trống của khách sạn.
     * 
     * @param hotelId  ID khách sạn
     * @param checkIn  Ngày nhận phòng
     * @param checkOut Ngày trả phòng
     * @param guests   Số lượng khách
     * @return ResponseEntity chứa List<RoomDTO>
     */
    @Operation(summary = "Get rooms by hotel", description = "Retrieves available rooms for a hotel based on check-in/out dates.")
    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<RoomDTO>> getRoomsByHotel(
            @Parameter(description = "ID of the hotel") @PathVariable @Min(1) Long hotelId,
            @Parameter(description = "Check-in date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @Parameter(description = "Check-out date (ISO format)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @Parameter(description = "Number of guests") @RequestParam(required = false, defaultValue = "1") Integer guests) {

        if (checkIn == null) {
            checkIn = LocalDate.now();
        }
        if (checkOut == null) {
            checkOut = checkIn.plusDays(1);
        }

        return ResponseEntity.ok(searchService.getRoomsByHotel(hotelId, checkIn, checkOut, guests));
    }

    /**
     * Đặt phòng khách sạn.
     * 
     * @param request        Thông tin đặt phòng
     * @param authentication Thông tin xác thực người dùng
     * @return ResponseEntity chứa HotelBookingDTO
     */
    @Operation(summary = "Book a hotel", description = "Creates a new hotel booking.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/book")
    public ResponseEntity<HotelBookingDTO> bookHotel(
            @Valid @RequestBody BookHotelRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.bookHotel(user.getId(), request));
    }

    /**
     * Lấy lịch sử đặt phòng của người dùng hiện tại.
     * 
     * @param authentication Thông tin xác thực người dùng
     * @return ResponseEntity chứa List<HotelBookingDTO>
     */
    @Operation(summary = "Get user bookings", description = "Retrieves all hotel bookings for the authenticated user.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/bookings")
    public ResponseEntity<List<HotelBookingDTO>> getUserBookings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.getUserHotelBookings(user.getId()));
    }

    /**
     * Lấy danh sách đặt phòng của các khách sạn thuộc đối tác.
     *
     * @param authentication Thông tin xác thực người dùng (Partner)
     * @return ResponseEntity chứa List<HotelBookingDTO>
     */
    @Operation(summary = "Get partner bookings", description = "Retrieves bookings for hotels owned by the authenticated partner (matched by email).")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/partner/bookings")
    public ResponseEntity<List<HotelBookingDTO>> getPartnerBookings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        // Use user's email to find owned hotels
        return ResponseEntity.ok(bookingService.getPartnerBookings(user.getEmail()));
    }

    /**
     * Lấy chi tiết một đơn đặt phòng.
     * 
     * @param bookingId      ID đơn đặt phòng
     * @param authentication Thông tin xác thực người dùng
     * @return ResponseEntity chứa HotelBookingDTO
     */
    @Operation(summary = "Get booking by ID", description = "Retrieves a specific hotel booking by ID.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<HotelBookingDTO> getBookingById(
            @Parameter(description = "ID of the booking") @PathVariable @Min(1) Long bookingId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.getHotelBookingById(bookingId, user.getId()));
    }

    /**
     * Hủy đơn đặt phòng.
     * 
     * @param bookingId      ID đơn đặt phòng
     * @param authentication Thông tin xác thực người dùng
     * @return ResponseEntity chứa HotelBookingDTO đã hủy
     */
    @Operation(summary = "Cancel booking", description = "Cancels a specific hotel booking.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<HotelBookingDTO> cancelBooking(
            @Parameter(description = "ID of the booking") @PathVariable @Min(1) Long bookingId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookingService.cancelHotelBooking(bookingId, user.getId()));
    }
}