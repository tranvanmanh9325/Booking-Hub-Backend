package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.enums.BookingStatus;
import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.ConflictException;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.mapper.BookingMapper;
import com.example.booking.model.*;
import com.example.booking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class BookingServiceTest {

    @Mock
    private HotelBookingRepository hotelBookingRepository;
    @Mock
    private MovieBookingRepository movieBookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ShowtimeRepository showtimeRepository;
    @Mock
    private BookingSeatRepository bookingSeatRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private Hotel testHotel;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");

        testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Test Hotel");

        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setHotel(testHotel);
        testRoom.setMaxGuests(2);
        testRoom.setPricePerNight(100.0);
    }

    @Test
    void bookHotel_Success() {
        BookHotelRequest request = new BookHotelRequest();
        request.setHotelId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));
        request.setGuests(2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(hotelBookingRepository.findConflictingBookings(any(), any(), any())).thenReturn(Collections.emptyList());

        HotelBooking savedBooking = new HotelBooking();
        savedBooking.setId(1L);
        savedBooking.setStatus(BookingStatus.PENDING);
        when(hotelBookingRepository.save(any(HotelBooking.class))).thenReturn(savedBooking);

        HotelBookingDTO expectedDto = new HotelBookingDTO();
        expectedDto.setId(1L);
        expectedDto.setStatus(BookingStatus.PENDING);
        when(bookingMapper.toHotelBookingDTO(any(HotelBooking.class))).thenReturn(expectedDto);

        HotelBookingDTO result = bookingService.bookHotel(1L, request);

        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());
        verify(emailService, times(1)).sendHotelBookingConfirmation(any(), any(), any());
    }

    @Test
    void bookHotel_UserNotFound_ThrowsException() {
        BookHotelRequest request = new BookHotelRequest();
        request.setHotelId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.bookHotel(1L, request));
    }

    @Test
    void bookHotel_RoomCapacityExceeded_ThrowsException() {
        BookHotelRequest request = new BookHotelRequest();
        request.setHotelId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));
        request.setGuests(3); // Max is 2

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(hotelBookingRepository.findConflictingBookings(any(), any(), any())).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> bookingService.bookHotel(1L, request));
    }

    @Test
    void bookHotel_ConflictingDates_ThrowsException() {
        BookHotelRequest request = new BookHotelRequest();
        request.setHotelId(1L);
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));
        request.setGuests(2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));

        HotelBooking conflictingBooking = new HotelBooking();
        when(hotelBookingRepository.findConflictingBookings(any(), any(), any()))
                .thenReturn(List.of(conflictingBooking));

        assertThrows(ConflictException.class, () -> bookingService.bookHotel(1L, request));
    }
}
