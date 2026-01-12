package com.example.booking.controller;

// DTO Imports
import com.example.booking.dto.PaymentDTO;
import com.example.booking.dto.PaymentRequest;
import com.example.booking.service.PaymentService;
import com.example.booking.enums.BookingType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

/**
 * Controller quản lý thanh toán và lịch sử giao dịch.
 * Xử lý thanh toán cho cả dịch vụ đặt phòng và đặt vé.
 */
@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*")
@Validated
@Tag(name = "Payments", description = "Payment processing and history APIs")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Xử lý thanh toán.
     * 
     * @param request Thông tin thanh toán
     * @return ResponseEntity chứa PaymentDTO
     */
    @Operation(summary = "Process payment", description = "Processes a payment for a booking.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<PaymentDTO> processPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    /**
     * Lấy thông tin thanh toán theo ID.
     * 
     * @param paymentId ID thanh toán
     * @return ResponseEntity chứa PaymentDTO
     */
    @Operation(summary = "Get payment by ID", description = "Retrieves details of a specific payment.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(
            @Parameter(description = "ID of the payment") @PathVariable @Min(1) Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    /**
     * Lấy thông tin thanh toán theo mã giao dịch.
     * 
     * @param transactionId Mã giao dịch
     * @return ResponseEntity chứa PaymentDTO
     */
    @Operation(summary = "Get payment by Transaction ID", description = "Retrieves payment details using the transaction reference.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentDTO> getPaymentByTransactionId(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.getPaymentByTransactionId(transactionId));
    }

    /**
     * Lấy danh sách thanh toán của một booking.
     * 
     * @param bookingId   ID booking
     * @param bookingType Loại booking (HOTEL/MOVIE)
     * @return ResponseEntity chứa List<PaymentDTO>
     */
    @Operation(summary = "Get payments by booking", description = "Retrieves all payments associated with a specific booking.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByBooking(
            @Parameter(description = "ID of the booking") @PathVariable @Min(1) Long bookingId,
            @Parameter(description = "Type of booking (HOTEL/MOVIE)") @RequestParam BookingType bookingType) {
        return ResponseEntity.ok(paymentService.getPaymentsByBooking(bookingId, bookingType));
    }

    /**
     * Hoàn tiền cho một giao dịch.
     * 
     * @param paymentId ID thanh toán
     * @return ResponseEntity chứa PaymentDTO đã hoàn tiền
     */
    @Operation(summary = "Refund payment", description = "Initiates a refund for a payment.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentDTO> refundPayment(
            @Parameter(description = "ID of the payment") @PathVariable @Min(1) Long paymentId) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId));
    }
}