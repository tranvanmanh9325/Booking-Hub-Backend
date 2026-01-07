package com.example.booking.repository;

import com.example.booking.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    @Query("SELECT s FROM Showtime s JOIN FETCH s.screen sc JOIN FETCH sc.cinema WHERE s.movie.id = :movieId")
    List<Showtime> findByMovieId(@Param("movieId") Long movieId);

    List<Showtime> findByScreenId(Long screenId);

    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.startTime >= :startDate AND s.startTime < :endDate ORDER BY s.startTime ASC")
    List<Showtime> findByMovieIdAndDateRange(@Param("movieId") Long movieId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}