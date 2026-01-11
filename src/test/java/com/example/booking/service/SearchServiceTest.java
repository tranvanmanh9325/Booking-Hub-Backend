package com.example.booking.service;

import com.example.booking.dto.HotelDTO;
import com.example.booking.dto.MovieDTO;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.mapper.HotelMapper;
import com.example.booking.mapper.MovieMapper;
import com.example.booking.model.Hotel;
import com.example.booking.model.Movie;
import com.example.booking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class SearchServiceTest {

    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private CinemaRepository cinemaRepository;
    @Mock
    private ShowtimeRepository showtimeRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private HotelBookingRepository hotelBookingRepository;
    @Mock
    private BookingSeatRepository bookingSeatRepository;
    @Mock
    private ReviewService reviewService;
    @Mock
    private HotelMapper hotelMapper;
    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private SearchService searchService;

    private Hotel testHotel;
    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Test Hotel");

        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");
    }

    @Test
    void getAllHotels_Success() {
        Page<Hotel> hotelPage = new PageImpl<>(List.of(testHotel));
        when(hotelRepository.findAll(any(Pageable.class))).thenReturn(hotelPage);
        when(reviewService.getAverageRatingsByHotelIds(anyList())).thenReturn(Map.of(1L, 4.5));

        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(1L);
        hotelDTO.setAverageRating(4.5);
        when(hotelMapper.toHotelDTO(any(Hotel.class), eq(4.5))).thenReturn(hotelDTO);

        Page<HotelDTO> result = searchService.getAllHotels(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(4.5, result.getContent().get(0).getAverageRating());
    }

    @Test
    void getHotelById_Success() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        when(reviewService.getHotelAverageRating(1L)).thenReturn(4.8);

        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(1L);
        when(hotelMapper.toHotelDTO(testHotel, 4.8)).thenReturn(hotelDTO);

        HotelDTO result = searchService.getHotelById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getHotelById_NotFound_ThrowsException() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> searchService.getHotelById(1L));
    }

    @Test
    void searchMovies_Success() {
        when(movieRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(List.of(testMovie));

        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(1L);
        when(movieMapper.toMovieDTO(testMovie)).thenReturn(movieDTO);

        List<MovieDTO> result = searchService.searchMovies("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}
