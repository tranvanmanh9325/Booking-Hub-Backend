package com.example.booking.service;

import com.example.booking.dto.HotelRatingDTO;
import com.example.booking.repository.HotelReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final HotelReviewRepository hotelReviewRepository;

    public ReviewService(HotelReviewRepository hotelReviewRepository) {
        this.hotelReviewRepository = hotelReviewRepository;
    }

    public Double getHotelAverageRating(Long hotelId) {
        return hotelReviewRepository.getAverageRatingByHotelId(hotelId);
    }

    public Map<Long, Double> getAverageRatingsByHotelIds(List<Long> hotelIds) {
        List<HotelRatingDTO> ratings = hotelReviewRepository.getAverageRatingsByHotelIds(hotelIds);
        return ratings.stream()
                .collect(Collectors.toMap(HotelRatingDTO::getHotelId, HotelRatingDTO::getAverageRating));
    }
}
