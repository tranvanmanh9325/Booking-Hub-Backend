package com.example.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "content")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // e.g., Product, Ticket, Promotion

    @Column(nullable = true)
    private String price; // Keeping as String for flexibility based on frontend, or could be BigDecimal

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String status; // active, inactive

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "images", columnDefinition = "TEXT")
    private String images; // JSON array of image URLs

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "release_date")
    private String releaseDate;

    @Column(name = "location")
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
}
