package com.example.booking.repository;

import com.example.booking.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCity(String city);

    List<Hotel> findByNameContainingIgnoreCase(String name);

    List<Hotel> findByStarRating(Integer starRating);

    List<Hotel> findByEmail(String email);
}