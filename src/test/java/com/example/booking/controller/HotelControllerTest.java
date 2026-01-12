package com.example.booking.controller;

import com.example.booking.dto.HotelDTO;
import com.example.booking.dto.HotelBookingDTO;
import com.example.booking.dto.BookHotelRequest;
import com.example.booking.model.User;
import com.example.booking.service.BookingService;
import com.example.booking.service.SearchService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HotelController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("null")
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @MockitoBean
    private BookingService bookingService;

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

    private HotelDTO testHotel;
    private User testUser;

    @BeforeEach
    void setUp() {
        testHotel = new HotelDTO();
        testHotel.setId(1L);
        testHotel.setName("Test Hotel");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    void getAllHotels_Success() throws Exception {
        Page<HotelDTO> hotelPage = new PageImpl<>(List.of(testHotel));
        when(searchService.getAllHotels(0, 10)).thenReturn(hotelPage);

        mockMvc.perform(get("/api/v1/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void getHotelById_Success() throws Exception {
        when(searchService.getHotelById(1L)).thenReturn(testHotel);

        mockMvc.perform(get("/api/v1/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void searchHotels_Success() throws Exception {
        when(searchService.searchHotels("Test")).thenReturn(List.of(testHotel));

        mockMvc.perform(get("/api/v1/hotels/search")
                .param("q", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void bookHotel_Success() throws Exception {
        // Setup Security Context with Custom User
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(testUser, null,
                Collections.emptyList());

        BookHotelRequest request = new BookHotelRequest();
        request.setHotelId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(2));
        request.setGuests(2);

        HotelBookingDTO bookingDTO = new HotelBookingDTO();
        bookingDTO.setId(1L);

        when(bookingService.bookHotel(eq(1L), any(BookHotelRequest.class))).thenReturn(bookingDTO);

        mockMvc.perform(post("/api/v1/hotels/book")
                .principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
