package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ShowtimeDTO {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long screenId;
    private String screenName;
    private Long cinemaId;
    private String cinemaName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double price;

    public ShowtimeDTO() {
    }

    public ShowtimeDTO(Long id, Long movieId, String movieTitle, Long screenId, String screenName, Long cinemaId,
            String cinemaName, LocalDateTime startTime, LocalDateTime endTime, Double price) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.screenId = screenId;
        this.screenName = screenName;
        this.cinemaId = cinemaId;
        this.cinemaName = cinemaName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ShowtimeDTO that = (ShowtimeDTO) o;
        return java.util.Objects.equals(id, that.id) &&
                java.util.Objects.equals(movieId, that.movieId) &&
                java.util.Objects.equals(movieTitle, that.movieTitle) &&
                java.util.Objects.equals(screenId, that.screenId) &&
                java.util.Objects.equals(screenName, that.screenName) &&
                java.util.Objects.equals(cinemaId, that.cinemaId) &&
                java.util.Objects.equals(cinemaName, that.cinemaName) &&
                java.util.Objects.equals(startTime, that.startTime) &&
                java.util.Objects.equals(endTime, that.endTime) &&
                java.util.Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, movieId, movieTitle, screenId, screenName, cinemaId, cinemaName, startTime,
                endTime, price);
    }

    @Override
    public String toString() {
        return "ShowtimeDTO{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", movieTitle='" + movieTitle + '\'' +
                ", screenId=" + screenId +
                ", screenName='" + screenName + '\'' +
                ", cinemaId=" + cinemaId +
                ", cinemaName='" + cinemaName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", price=" + price +
                '}';
    }
}