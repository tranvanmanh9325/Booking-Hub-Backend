package com.example.booking.repository;

import com.example.booking.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    
    List<Movie> findByTitleContainingIgnoreCase(String title);
    
    List<Movie> findByGenre(String genre);
    
    @Query("SELECT m FROM Movie m WHERE m.releaseDate <= CURRENT_TIMESTAMP ORDER BY m.releaseDate DESC")
    List<Movie> findNowShowing();
    
    @Query("SELECT m FROM Movie m WHERE m.releaseDate > CURRENT_TIMESTAMP ORDER BY m.releaseDate ASC")
    List<Movie> findUpcoming();
}