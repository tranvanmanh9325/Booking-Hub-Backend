package com.example.booking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BookMovieRequest {

    @NotNull(message = "Showtime ID is required")
    private Long showtimeId;

    @NotEmpty(message = "At least one seat must be selected")
    private List<Long> seatIds;

    public BookMovieRequest() {
    }

    public BookMovieRequest(Long showtimeId, List<Long> seatIds) {
        this.showtimeId = showtimeId;
        this.seatIds = seatIds;
    }

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
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