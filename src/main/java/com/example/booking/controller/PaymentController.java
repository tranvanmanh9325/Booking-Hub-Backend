package com.example.booking.controller;

import com.example.booking.dto.PaymentDTO;
import com.example.booking.dto.PaymentRequest;
import com.example.booking.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> processPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable @Min(1) Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentDTO> getPaymentByTransactionId(@PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.getPaymentByTransactionId(transactionId));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByBooking(
            @PathVariable @Min(1) Long bookingId,
            @RequestParam String bookingType) {
        return ResponseEntity.ok(paymentService.getPaymentsByBooking(bookingId, bookingType));
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentDTO> refundPayment(@PathVariable @Min(1) Long paymentId) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId));
    }
}