package com.example.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "movie_bookings")
public class MovieBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(nullable = false)
    private String status; // PENDING, CONFIRMED, CANCELLED, COMPLETED

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BookingSeat> bookingSeats;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (bookingDate == null) {
            bookingDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MovieBooking() {
    }

    public MovieBooking(Long id, User user, Showtime showtime, LocalDateTime bookingDate, String status,
            Double totalPrice, LocalDateTime createdAt, LocalDateTime updatedAt, List<BookingSeat> bookingSeats) {
        this.id = id;
        this.user = user;
        this.showtime = showtime;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.bookingSeats = bookingSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MovieBooking that = (MovieBooking) o;
        return java.util.Objects.equals(id, that.id) &&
                java.util.Objects.equals(user, that.user) &&
                java.util.Objects.equals(showtime, that.showtime) &&
                java.util.Objects.equals(bookingDate, that.bookingDate) &&
                java.util.Objects.equals(status, that.status) &&
                java.util.Objects.equals(totalPrice, that.totalPrice) &&
                java.util.Objects.equals(createdAt, that.createdAt) &&
                java.util.Objects.equals(updatedAt, that.updatedAt) &&
                java.util.Objects.equals(bookingSeats, that.bookingSeats);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, user, showtime, bookingDate, status, totalPrice, createdAt, updatedAt,
                bookingSeats);
    }

    @Override
    public String toString() {
        return "MovieBooking{" +
                "id=" + id +
                ", user=" + user +
                ", showtime=" + showtime +
                ", bookingDate=" + bookingDate +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", bookingSeats=" + bookingSeats +
                '}';
    }
}