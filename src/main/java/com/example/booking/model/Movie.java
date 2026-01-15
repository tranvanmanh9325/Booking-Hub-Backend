package com.example.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column
    private String genre;

    @Column
    private Integer duration; // in minutes

    @Column
    private Double rating;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "trailer_url")
    private String trailerUrl;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Showtime> showtimes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Movie() {
    }

    public Movie(Long id, String title, String description, String genre, Integer duration, Double rating,
            String posterUrl, String trailerUrl, LocalDateTime releaseDate, LocalDateTime createdAt,
            LocalDateTime updatedAt, List<Showtime> showtimes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
        this.releaseDate = releaseDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.showtimes = showtimes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Movie movie = (Movie) o;
        return java.util.Objects.equals(id, movie.id) &&
                java.util.Objects.equals(title, movie.title) &&
                java.util.Objects.equals(description, movie.description) &&
                java.util.Objects.equals(genre, movie.genre) &&
                java.util.Objects.equals(duration, movie.duration) &&
                java.util.Objects.equals(rating, movie.rating) &&
                java.util.Objects.equals(posterUrl, movie.posterUrl) &&
                java.util.Objects.equals(trailerUrl, movie.trailerUrl) &&
                java.util.Objects.equals(releaseDate, movie.releaseDate) &&
                java.util.Objects.equals(createdAt, movie.createdAt) &&
                java.util.Objects.equals(updatedAt, movie.updatedAt) &&
                java.util.Objects.equals(showtimes, movie.showtimes);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, title, description, genre, duration, rating, posterUrl, trailerUrl,
                releaseDate, createdAt, updatedAt, showtimes);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", rating=" + rating +
                ", posterUrl='" + posterUrl + '\'' +
                ", trailerUrl='" + trailerUrl + '\'' +
                ", releaseDate=" + releaseDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", showtimes=" + showtimes +
                '}';
    }
}