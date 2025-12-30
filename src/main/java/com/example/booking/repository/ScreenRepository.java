package com.example.booking.repository;

import com.example.booking.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    
    List<Screen> findByCinemaId(Long cinemaId);
}

