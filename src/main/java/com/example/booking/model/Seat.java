package com.example.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "\"row\"", nullable = false)
    private String row; // A, B, C, etc.

    @Column(nullable = false)
    private Integer number; // 1, 2, 3, etc.

    @Column(name = "seat_type")
    private String seatType; // Regular, VIP, Premium, etc.

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Seat() {
    }

    public Seat(Long id, Screen screen, String row, Integer number, String seatType, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.screen = screen;
        this.row = row;
        this.number = number;
        this.seatType = seatType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Seat seat = (Seat) o;
        return java.util.Objects.equals(id, seat.id) &&
                java.util.Objects.equals(screen, seat.screen) &&
                java.util.Objects.equals(row, seat.row) &&
                java.util.Objects.equals(number, seat.number) &&
                java.util.Objects.equals(seatType, seat.seatType) &&
                java.util.Objects.equals(createdAt, seat.createdAt) &&
                java.util.Objects.equals(updatedAt, seat.updatedAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, screen, row, number, seatType, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", screen=" + screen +
                ", row='" + row + '\'' +
                ", number=" + number +
                ", seatType='" + seatType + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}