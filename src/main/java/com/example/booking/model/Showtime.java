package com.example.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "showtimes")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Double price;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MovieBooking> bookings;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Showtime() {
    }

    public Showtime(Long id, Movie movie, Screen screen, LocalDateTime startTime, LocalDateTime endTime, Double price,
            LocalDateTime createdAt, LocalDateTime updatedAt, List<MovieBooking> bookings) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.bookings = bookings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Showtime showtime = (Showtime) o;
        return java.util.Objects.equals(id, showtime.id) &&
                java.util.Objects.equals(movie, showtime.movie) &&
                java.util.Objects.equals(screen, showtime.screen) &&
                java.util.Objects.equals(startTime, showtime.startTime) &&
                java.util.Objects.equals(endTime, showtime.endTime) &&
                java.util.Objects.equals(price, showtime.price) &&
                java.util.Objects.equals(createdAt, showtime.createdAt) &&
                java.util.Objects.equals(updatedAt, showtime.updatedAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, movie, screen, startTime, endTime, price, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Showtime{" +
                "id=" + id +
                ", movie=" + movie +
                ", screen=" + screen +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", price=" + price +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}