package com.example.booking.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_seats")
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private MovieBooking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    private Double price;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public BookingSeat() {
    }

    public BookingSeat(Long id, MovieBooking booking, Seat seat, Double price, LocalDateTime createdAt) {
        this.id = id;
        this.booking = booking;
        this.seat = seat;
        this.price = price;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovieBooking getBooking() {
        return booking;
    }

    public void setBooking(MovieBooking booking) {
        this.booking = booking;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BookingSeat that = (BookingSeat) o;
        return java.util.Objects.equals(id, that.id) &&
                java.util.Objects.equals(booking, that.booking) &&
                java.util.Objects.equals(seat, that.seat) &&
                java.util.Objects.equals(price, that.price) &&
                java.util.Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, booking, seat, price, createdAt);
    }

    @Override
    public String toString() {
        return "BookingSeat{" +
                "id=" + id +
                ", booking=" + booking +
                ", seat=" + seat +
                ", price=" + price +
                ", createdAt=" + createdAt +
                '}';
    }
}