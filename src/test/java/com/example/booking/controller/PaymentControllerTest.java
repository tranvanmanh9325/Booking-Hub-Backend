package com.example.booking.controller;

import com.example.booking.dto.PaymentDTO;
import com.example.booking.dto.PaymentRequest;
import com.example.booking.enums.BookingType;
import com.example.booking.enums.PaymentMethod;
import com.example.booking.enums.PaymentStatus;
import com.example.booking.service.PaymentService;
import com.example.booking.util.JwtUtil;
import com.example.booking.security.RateLimitFilter;
import com.example.booking.filter.MdcLoggingFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.booking.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for logic testing
@SuppressWarnings("null")
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private MdcLoggingFilter mdcLoggingFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentRequest testRequest;
    private PaymentDTO testResponse;

    @BeforeEach
    void setUp() {
        testRequest = new PaymentRequest();
        testRequest.setBookingId(1L);
        testRequest.setBookingType(BookingType.HOTEL);
        testRequest.setAmount(100.0);
        testRequest.setPaymentMethod(PaymentMethod.VISA);

        testResponse = new PaymentDTO();
        testResponse.setId(1L);
        testResponse.setBookingId(1L);
        testResponse.setAmount(100.0);
        testResponse.setStatus(PaymentStatus.SUCCESS);
    }

    @Test
    void processPayment_Success() throws Exception {
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getPaymentById_Success() throws Exception {
        when(paymentService.getPaymentById(1L)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getPaymentsByBooking_Success() throws Exception {
        when(paymentService.getPaymentsByBooking(eq(1L), eq(BookingType.HOTEL)))
                .thenReturn(java.util.List.of(testResponse));

        mockMvc.perform(get("/api/v1/payments/booking/1")
                .param("bookingType", "HOTEL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void refundPayment_Success() throws Exception {
        testResponse.setStatus(PaymentStatus.REFUNDED);
        when(paymentService.refundPayment(1L)).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/payments/1/refund"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));
    }
}
