package com.example.booking.repository;

import com.example.booking.model.HotelBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {
    
    List<HotelBooking> findByUserId(Long userId);
    
    List<HotelBooking> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT hb FROM HotelBooking hb WHERE hb.room.id = :roomId " +
           "AND hb.status IN ('PENDING', 'CONFIRMED') " +
           "AND ((hb.checkIn <= :checkIn AND hb.checkOut > :checkIn) OR " +
           "(hb.checkIn < :checkOut AND hb.checkOut >= :checkOut) OR " +
           "(hb.checkIn >= :checkIn AND hb.checkOut <= :checkOut))")
    List<HotelBooking> findConflictingBookings(@Param("roomId") Long roomId,
                                                @Param("checkIn") LocalDate checkIn,
                                                @Param("checkOut") LocalDate checkOut);
}

