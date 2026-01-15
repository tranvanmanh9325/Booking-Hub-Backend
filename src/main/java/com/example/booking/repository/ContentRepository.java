package com.example.booking.repository;

import com.example.booking.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    // Basic CRUD operations are provided by JpaRepository
}
