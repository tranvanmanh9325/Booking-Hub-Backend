package com.example.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "hotel_reviews")
public class HotelReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(length = 2000)
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public HotelReview() {
    }

    public HotelReview(Long id, Hotel hotel, User user, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.hotel = hotel;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HotelReview that = (HotelReview) o;
        return java.util.Objects.equals(id, that.id) &&
                java.util.Objects.equals(hotel, that.hotel) &&
                java.util.Objects.equals(user, that.user) &&
                java.util.Objects.equals(rating, that.rating) &&
                java.util.Objects.equals(comment, that.comment) &&
                java.util.Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, hotel, user, rating, comment, createdAt);
    }

    @Override
    public String toString() {
        return "HotelReview{" +
                "id=" + id +
                ", hotel=" + hotel +
                ", user=" + user +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}