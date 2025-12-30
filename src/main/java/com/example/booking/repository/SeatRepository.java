package com.example.booking.repository;

import com.example.booking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    List<Seat> findByScreenId(Long screenId);
    
    @Query("SELECT s FROM Seat s WHERE s.screen.id = :screenId AND s.row = :row AND s.number = :number")
    Optional<Seat> findByScreenIdAndRowAndNumber(@Param("screenId") Long screenId, 
                                                  @Param("row") String row, 
                                                  @Param("number") Integer number);
}

