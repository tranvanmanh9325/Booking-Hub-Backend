package com.example.booking.repository;

import com.example.booking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByHotelId(Long hotelId);
    
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.maxGuests >= :guests")
    List<Room> findAvailableRoomsByHotelAndGuests(@Param("hotelId") Long hotelId, @Param("guests") Integer guests);
}