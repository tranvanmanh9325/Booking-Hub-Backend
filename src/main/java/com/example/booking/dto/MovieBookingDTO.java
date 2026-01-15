package com.example.booking.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.example.booking.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MovieBookingDTO {
    private Long id;
    private Long userId;
    private Long showtimeId;
    private String movieTitle;
    private String cinemaName;
    private String screenName;
    private LocalDateTime showtimeStart;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private Double totalPrice;
    private List<SeatDTO> seats;

    public MovieBookingDTO() {
    }

    public MovieBookingDTO(Long id, Long userId, Long showtimeId, String movieTitle, String cinemaName,
            String screenName, LocalDateTime showtimeStart, LocalDateTime bookingDate, BookingStatus status,
            Double totalPrice,
            List<SeatDTO> seats) {
        this.id = id;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.movieTitle = movieTitle;
        this.cinemaName = cinemaName;
        this.screenName = screenName;
        this.showtimeStart = showtimeStart;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.seats = seats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MovieBookingDTO that = (MovieBookingDTO) o;
        return java.util.Objects.equals(id, that.id) &&
                java.util.Objects.equals(userId, that.userId) &&
                java.util.Objects.equals(showtimeId, that.showtimeId) &&
                java.util.Objects.equals(movieTitle, that.movieTitle) &&
                java.util.Objects.equals(cinemaName, that.cinemaName) &&
                java.util.Objects.equals(screenName, that.screenName) &&
                java.util.Objects.equals(showtimeStart, that.showtimeStart) &&
                java.util.Objects.equals(bookingDate, that.bookingDate) &&
                java.util.Objects.equals(status, that.status) &&
                java.util.Objects.equals(totalPrice, that.totalPrice) &&
                java.util.Objects.equals(seats, that.seats);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, userId, showtimeId, movieTitle, cinemaName, screenName, showtimeStart,
                bookingDate, status, totalPrice, seats);
    }

    @Override
    public String toString() {
        return "MovieBookingDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", showtimeId=" + showtimeId +
                ", movieTitle='" + movieTitle + '\'' +
                ", cinemaName='" + cinemaName + '\'' +
                ", screenName='" + screenName + '\'' +
                ", showtimeStart=" + showtimeStart +
                ", bookingDate=" + bookingDate +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                ", seats=" + seats +
                '}';
    }
}