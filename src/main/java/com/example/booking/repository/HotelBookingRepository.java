package com.example.booking.repository;

import com.example.booking.model.HotelBooking;
import com.example.booking.enums.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {

       void deleteByUser(com.example.booking.model.User user);

       List<HotelBooking> findByUserId(Long userId);

       @Query("SELECT hb FROM HotelBooking hb LEFT JOIN FETCH hb.hotel LEFT JOIN FETCH hb.room WHERE hb.user.id = :userId ORDER BY hb.createdAt DESC")
       List<HotelBooking> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

       @Query("SELECT hb FROM HotelBooking hb WHERE hb.room.id = :roomId " +
                     "AND hb.status IN ('PENDING', 'CONFIRMED') " +
                     "AND ((hb.checkIn <= :checkIn AND hb.checkOut > :checkIn) OR " +
                     "(hb.checkIn < :checkOut AND hb.checkOut >= :checkOut) OR " +
                     "(hb.checkIn >= :checkIn AND hb.checkOut <= :checkOut))")
       List<HotelBooking> findConflictingBookings(@Param("roomId") Long roomId,
                     @Param("checkIn") LocalDate checkIn,
                     @Param("checkOut") LocalDate checkOut);

       @Query("SELECT hb FROM HotelBooking hb WHERE hb.room.id IN :roomIds " +
                     "AND hb.status IN ('PENDING', 'CONFIRMED') " +
                     "AND ((hb.checkIn <= :checkIn AND hb.checkOut > :checkIn) OR " +
                     "(hb.checkIn < :checkOut AND hb.checkOut >= :checkOut) OR " +
                     "(hb.checkIn >= :checkIn AND hb.checkOut <= :checkOut))")
       List<HotelBooking> findConflictingBookingsForRooms(@Param("roomIds") List<Long> roomIds,
                     @Param("checkIn") LocalDate checkIn,
                     @Param("checkOut") LocalDate checkOut);

       @Query("SELECT hb FROM HotelBooking hb WHERE hb.checkIn = :checkIn AND hb.status = :status")
       List<HotelBooking> findByCheckInAndStatus(@Param("checkIn") LocalDate checkIn,
                     @Param("status") BookingStatus status);

       List<HotelBooking> findByHotelInOrderByCreatedAtDesc(List<com.example.booking.model.Hotel> hotels);
}