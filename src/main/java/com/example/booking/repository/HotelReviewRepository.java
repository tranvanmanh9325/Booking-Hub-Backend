package com.example.booking.repository;

import com.example.booking.model.HotelReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelReviewRepository extends JpaRepository<HotelReview, Long> {

    void deleteByUser(com.example.booking.model.User user);

    List<HotelReview> findByHotelId(Long hotelId);

    Optional<HotelReview> findByHotelIdAndUserId(Long hotelId, Long userId);

    @Query("SELECT AVG(hr.rating) FROM HotelReview hr WHERE hr.hotel.id = :hotelId")
    Double getAverageRatingByHotelId(@Param("hotelId") Long hotelId);

    @Query("SELECT new com.example.booking.dto.HotelRatingDTO(hr.hotel.id, AVG(hr.rating)) FROM HotelReview hr GROUP BY hr.hotel.id")
    List<com.example.booking.dto.HotelRatingDTO> getAllAverageRatings();

    @Query("SELECT new com.example.booking.dto.HotelRatingDTO(hr.hotel.id, AVG(hr.rating)) FROM HotelReview hr WHERE hr.hotel.id IN :hotelIds GROUP BY hr.hotel.id")
    List<com.example.booking.dto.HotelRatingDTO> getAverageRatingsByHotelIds(@Param("hotelIds") List<Long> hotelIds);
}