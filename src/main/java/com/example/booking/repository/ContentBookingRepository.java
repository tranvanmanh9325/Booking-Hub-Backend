package com.example.booking.repository;

import com.example.booking.model.Content;
import com.example.booking.model.ContentBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentBookingRepository extends JpaRepository<ContentBooking, Long> {
    List<ContentBooking> findByContentInOrderByBookingDateDesc(List<Content> contents);
}