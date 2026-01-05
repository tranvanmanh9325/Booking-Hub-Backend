package com.example.booking.repository;

import com.example.booking.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CinemaRepository extends JpaRepository<Cinema, Long> {
    
    List<Cinema> findByCity(String city);
    
    List<Cinema> findByNameContainingIgnoreCase(String name);
}