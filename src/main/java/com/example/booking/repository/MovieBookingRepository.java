package com.example.booking.repository;

import com.example.booking.model.MovieBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieBookingRepository extends JpaRepository<MovieBooking, Long> {

    List<MovieBooking> findByUserId(Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT mb FROM MovieBooking mb JOIN FETCH mb.showtime s JOIN FETCH s.movie JOIN FETCH s.screen sc JOIN FETCH sc.cinema WHERE mb.user.id = :userId ORDER BY mb.createdAt DESC")
    List<MovieBooking> findByUserIdOrderByCreatedAtDesc(
            @org.springframework.data.repository.query.Param("userId") Long userId);

    List<MovieBooking> findByShowtimeId(Long showtimeId);

    @org.springframework.data.jpa.repository.Query("SELECT b FROM MovieBooking b WHERE b.showtime.startTime BETWEEN :start AND :end AND b.status = :status")
    List<MovieBooking> findByShowtimeStartTimeBetweenAndStatus(java.time.LocalDateTime start,
            java.time.LocalDateTime end, String status);
}