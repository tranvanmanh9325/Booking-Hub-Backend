package com.example.booking.repository;

import com.example.booking.model.Content;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    @EntityGraph(attributePaths = "owner")
    List<Content> findByOwnerEmail(String email);

    @Override
    @EntityGraph(attributePaths = "owner")
    @NonNull
    List<Content> findAll();

    @Override
    @EntityGraph(attributePaths = "owner")
    @NonNull
    Optional<Content> findById(@NonNull Long id);

    List<Content> findByTypeInAndStatus(List<String> types, String status);
}