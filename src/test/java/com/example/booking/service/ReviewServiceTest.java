package com.example.booking.service;

import com.example.booking.dto.HotelRatingDTO;
import com.example.booking.repository.HotelReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private HotelReviewRepository hotelReviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void getHotelAverageRating_Success() {
        when(hotelReviewRepository.getAverageRatingByHotelId(1L)).thenReturn(4.5);

        Double rating = reviewService.getHotelAverageRating(1L);

        assertEquals(4.5, rating);
    }

    @Test
    void getAverageRatingsByHotelIds_Success() {
        HotelRatingDTO rating1 = new HotelRatingDTO(1L, 4.5);
        HotelRatingDTO rating2 = new HotelRatingDTO(2L, 4.0);

        when(hotelReviewRepository.getAverageRatingsByHotelIds(List.of(1L, 2L)))
                .thenReturn(List.of(rating1, rating2));

        Map<Long, Double> result = reviewService.getAverageRatingsByHotelIds(List.of(1L, 2L));

        assertEquals(2, result.size());
        assertEquals(4.5, result.get(1L));
        assertEquals(4.0, result.get(2L));
    }
}
