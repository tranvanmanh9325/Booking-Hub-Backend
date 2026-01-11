package com.example.booking.service;

import com.example.booking.dto.PaymentDTO;
import com.example.booking.dto.PaymentRequest;
import com.example.booking.enums.BookingStatus;
import com.example.booking.enums.BookingType;
import com.example.booking.enums.PaymentMethod;
import com.example.booking.enums.PaymentStatus;
import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.mapper.PaymentMapper;
import com.example.booking.model.HotelBooking;
import com.example.booking.model.MovieBooking;
import com.example.booking.model.Payment;
import com.example.booking.repository.HotelBookingRepository;
import com.example.booking.repository.MovieBookingRepository;
import com.example.booking.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private MovieBookingRepository movieBookingRepository;
    @Mock
    private HotelBookingRepository hotelBookingRepository;
    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest testRequest;
    private HotelBooking testHotelBooking;
    private MovieBooking testMovieBooking;

    @BeforeEach
    void setUp() {
        testRequest = new PaymentRequest();
        testRequest.setBookingId(1L);
        testRequest.setBookingType(BookingType.HOTEL);
        testRequest.setAmount(100.0);
        testRequest.setPaymentMethod(PaymentMethod.VISA);

        testHotelBooking = new HotelBooking();
        testHotelBooking.setId(1L);
        testHotelBooking.setTotalPrice(100.0);
        testHotelBooking.setStatus(BookingStatus.PENDING);

        testMovieBooking = new MovieBooking();
        testMovieBooking.setId(1L);
        testMovieBooking.setTotalPrice(50.0);
        testMovieBooking.setStatus(BookingStatus.PENDING);
    }

    @Test
    void processPayment_Hotel_Success() {
        when(hotelBookingRepository.findById(1L)).thenReturn(Optional.of(testHotelBooking));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setId(1L);
        paymentDTO.setStatus(PaymentStatus.SUCCESS);
        when(paymentMapper.toPaymentDTO(any(Payment.class))).thenReturn(paymentDTO);

        PaymentDTO result = paymentService.processPayment(testRequest);

        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        verify(hotelBookingRepository).save(argThat(booking -> booking.getStatus() == BookingStatus.CONFIRMED));
    }

    @Test
    void processPayment_AmountMismatch_ThrowsException() {
        testRequest.setAmount(50.0); // Mismatch, booking is 100.0
        when(hotelBookingRepository.findById(1L)).thenReturn(Optional.of(testHotelBooking));

        assertThrows(BadRequestException.class, () -> paymentService.processPayment(testRequest));
    }

    @Test
    void processPayment_BookingNotFound_ThrowsException() {
        when(hotelBookingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> paymentService.processPayment(testRequest));
    }

    @Test
    void refundPayment_Success() {
        Payment successfulPayment = new Payment();
        successfulPayment.setId(1L);
        successfulPayment.setStatus(PaymentStatus.SUCCESS);
        successfulPayment.setBookingId(1L);
        successfulPayment.setBookingType(BookingType.HOTEL);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(successfulPayment));
        when(hotelBookingRepository.findById(1L)).thenReturn(Optional.of(testHotelBooking)); // For status update
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setStatus(PaymentStatus.REFUNDED);
        when(paymentMapper.toPaymentDTO(any(Payment.class))).thenReturn(paymentDTO);

        PaymentDTO result = paymentService.refundPayment(1L);

        assertEquals(PaymentStatus.REFUNDED, result.getStatus());
        verify(hotelBookingRepository).save(argThat(booking -> booking.getStatus() == BookingStatus.CANCELLED));
    }

    @Test
    void refundPayment_NotSuccess_ThrowsException() {
        Payment pendingPayment = new Payment();
        pendingPayment.setId(1L);
        pendingPayment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(pendingPayment));

        assertThrows(BadRequestException.class, () -> paymentService.refundPayment(1L));
    }
}
