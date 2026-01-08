package com.example.booking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BookMovieRequest {

    @NotNull(message = "Showtime ID is required")
    @jakarta.validation.constraints.Min(value = 1, message = "Showtime ID must be positive")
    private Long showtimeId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<Long> seatIds;

    public BookMovieRequest() {
    }

    public BookMovieRequest(Long showtimeId, List<Long> seatIds) {
        this.showtimeId = showtimeId;
        this.seatIds = seatIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BookMovieRequest that = (BookMovieRequest) o;
        return java.util.Objects.equals(showtimeId, that.showtimeId) &&
                java.util.Objects.equals(seatIds, that.seatIds);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(showtimeId, seatIds);
    }

    @Override
    public String toString() {
        return "BookMovieRequest{" +
                "showtimeId=" + showtimeId +
                ", seatIds=" + seatIds +
                '}';
    }
}