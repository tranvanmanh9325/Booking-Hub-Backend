package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class MovieDTO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String title;
    private String description;
    private String genre;
    private Integer duration;
    private Double rating;
    private String posterUrl;
    private String trailerUrl;
    private LocalDateTime releaseDate;

    public MovieDTO() {
    }

    public MovieDTO(Long id, String title, String description, String genre, Integer duration, Double rating,
            String posterUrl, String trailerUrl, LocalDateTime releaseDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
        this.releaseDate = releaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MovieDTO movieDTO = (MovieDTO) o;
        return java.util.Objects.equals(id, movieDTO.id) &&
                java.util.Objects.equals(title, movieDTO.title) &&
                java.util.Objects.equals(description, movieDTO.description) &&
                java.util.Objects.equals(genre, movieDTO.genre) &&
                java.util.Objects.equals(duration, movieDTO.duration) &&
                java.util.Objects.equals(rating, movieDTO.rating) &&
                java.util.Objects.equals(posterUrl, movieDTO.posterUrl) &&
                java.util.Objects.equals(trailerUrl, movieDTO.trailerUrl) &&
                java.util.Objects.equals(releaseDate, movieDTO.releaseDate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, title, description, genre, duration, rating, posterUrl, trailerUrl,
                releaseDate);
    }

    @Override
    public String toString() {
        return "MovieDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", rating=" + rating +
                ", posterUrl='" + posterUrl + '\'' +
                ", trailerUrl='" + trailerUrl + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}