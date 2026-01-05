package com.example.booking.repository;

import com.example.booking.model.MovieBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieBookingRepository extends JpaRepository<MovieBooking, Long> {
    
    List<MovieBooking> findByUserId(Long userId);
    
    List<MovieBooking> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<MovieBooking> findByShowtimeId(Long showtimeId);
}