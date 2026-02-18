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

        // Existing method - might need to check usage
        List<Showtime> findByMovieId(Long movieId);

        // New method for Content
        List<Showtime> findByContentId(Long contentId);

        @Query("SELECT s FROM Showtime s WHERE s.screen.id = :screenId AND " +
                        "((s.startTime <= :endTime AND s.endTime >= :startTime))")
        List<Showtime> findConflictingShowtimes(@Param("screenId") Long screenId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);
}