package com.example.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "screens")
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false)
    private String name;

    @Column
    private Integer capacity;

    @Column(name = "screen_type")
    private String screenType; // IMAX, 3D, Standard, etc.

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Showtime> showtimes;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Screen() {
    }

    public Screen(Long id, Cinema cinema, String name, Integer capacity, String screenType, LocalDateTime createdAt,
            LocalDateTime updatedAt, List<Showtime> showtimes, List<Seat> seats) {
        this.id = id;
        this.cinema = cinema;
        this.name = name;
        this.capacity = capacity;
        this.screenType = screenType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.showtimes = showtimes;
        this.seats = seats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Screen screen = (Screen) o;
        return java.util.Objects.equals(id, screen.id) &&
                java.util.Objects.equals(cinema, screen.cinema) &&
                java.util.Objects.equals(name, screen.name) &&
                java.util.Objects.equals(capacity, screen.capacity) &&
                java.util.Objects.equals(screenType, screen.screenType) &&
                java.util.Objects.equals(createdAt, screen.createdAt) &&
                java.util.Objects.equals(updatedAt, screen.updatedAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, cinema, name, capacity, screenType, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Screen{" +
                "id=" + id +
                ", cinema=" + cinema +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", screenType='" + screenType + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}