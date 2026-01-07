package com.example.booking.service;

import com.example.booking.dto.PaymentDTO;
import com.example.booking.dto.PaymentRequest;
import com.example.booking.model.MovieBooking;
import com.example.booking.model.HotelBooking;
import com.example.booking.model.Payment;
import com.example.booking.repository.*;

import com.example.booking.exception.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MovieBookingRepository movieBookingRepository;
    private final HotelBookingRepository hotelBookingRepository;

    public PaymentService(PaymentRepository paymentRepository, MovieBookingRepository movieBookingRepository,
            HotelBookingRepository hotelBookingRepository) {
        this.paymentRepository = paymentRepository;
        this.movieBookingRepository = movieBookingRepository;
        this.hotelBookingRepository = hotelBookingRepository;
    }

    @Transactional
    public PaymentDTO processPayment(PaymentRequest request) {
        // Validate booking exists and get amount
        Double bookingAmount = validateBookingAndGetAmount(request.getBookingId(), request.getBookingType());

        if (!request.getAmount().equals(bookingAmount)) {
            throw new BadRequestException("Payment amount does not match booking amount");
        }

        // Create payment record
        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setBookingType(request.getBookingType());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("PENDING");

        payment = paymentRepository.save(payment);

        // Simulate payment processing (in real app, this would call payment gateway)
        try {
            // Simulate payment gateway call
            Thread.sleep(500); // Simulate network delay

            // For demo purposes, always succeed
            payment.setStatus("SUCCESS");
            payment.setPaidAt(LocalDateTime.now());

            // Update booking status
            updateBookingStatus(request.getBookingId(), request.getBookingType(), "CONFIRMED");

        } catch (Exception e) {
            payment.setStatus("FAILED");
        }

        payment = paymentRepository.save(payment);

        return convertToDTO(payment);
    }

    public PaymentDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(java.util.Objects.requireNonNull(paymentId))
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return convertToDTO(payment);
    }

    public PaymentDTO getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return convertToDTO(payment);
    }

    public List<PaymentDTO> getPaymentsByBooking(Long bookingId, String bookingType) {
        return paymentRepository.findByBookingIdAndBookingType(bookingId, bookingType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentDTO refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(java.util.Objects.requireNonNull(paymentId))
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (!payment.getStatus().equals("SUCCESS")) {
            throw new BadRequestException("Can only refund successful payments");
        }

        // Simulate refund processing
        payment.setStatus("REFUNDED");
        payment = paymentRepository.save(payment);

        // Update booking status
        updateBookingStatus(payment.getBookingId(), payment.getBookingType(), "CANCELLED");

        return convertToDTO(payment);
    }

    private Double validateBookingAndGetAmount(Long bookingId, String bookingType) {
        switch (bookingType.toUpperCase()) {
            case "MOVIE":
                MovieBooking movieBooking = movieBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                        .orElseThrow(() -> new ResourceNotFoundException("Movie booking not found"));
                if (!movieBooking.getStatus().equals("PENDING")) {
                    throw new BadRequestException("Booking is not in PENDING status");
                }
                return movieBooking.getTotalPrice();

            case "HOTEL":
                HotelBooking hotelBooking = hotelBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                        .orElseThrow(() -> new ResourceNotFoundException("Hotel booking not found"));
                if (!hotelBooking.getStatus().equals("PENDING")) {
                    throw new BadRequestException("Booking is not in PENDING status");
                }
                return hotelBooking.getTotalPrice();

            default:
                throw new BadRequestException("Unsupported booking type: " + bookingType);
        }
    }

    private void updateBookingStatus(Long bookingId, String bookingType, String status) {
        switch (bookingType.toUpperCase()) {
            case "MOVIE":
                MovieBooking movieBooking = movieBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                        .orElseThrow(() -> new ResourceNotFoundException("Movie booking not found"));
                movieBooking.setStatus(status);
                movieBookingRepository.save(movieBooking);
                break;

            case "HOTEL":
                HotelBooking hotelBooking = hotelBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                        .orElseThrow(() -> new ResourceNotFoundException("Hotel booking not found"));
                hotelBooking.setStatus(status);
                hotelBookingRepository.save(hotelBooking);
                break;
        }
    }

    private PaymentDTO convertToDTO(Payment payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getBookingId(),
                payment.getBookingType(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getStatus(),
                payment.getPaidAt(),
                payment.getCreatedAt());
    }
}