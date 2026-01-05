package com.example.booking.repository;

import com.example.booking.model.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    
    List<BookingSeat> findByBookingId(Long bookingId);
    
    @Query("SELECT bs.seat.id FROM BookingSeat bs " +
           "JOIN bs.booking b " +
           "WHERE b.showtime.id = :showtimeId AND b.status IN ('PENDING', 'CONFIRMED')")
    List<Long> findBookedSeatIdsByShowtimeId(@Param("showtimeId") Long showtimeId);
}