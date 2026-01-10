package com.example.booking.mapper;

import com.example.booking.dto.CinemaDTO;
import com.example.booking.dto.MovieDTO;
import com.example.booking.dto.SeatDTO;
import com.example.booking.dto.ShowtimeDTO;
import com.example.booking.model.Cinema;
import com.example.booking.model.Movie;
import com.example.booking.model.Seat;
import com.example.booking.model.Showtime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieDTO toMovieDTO(Movie movie);

    CinemaDTO toCinemaDTO(Cinema cinema);

    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "movieTitle", source = "movie.title")
    @Mapping(target = "screenId", source = "screen.id")
    @Mapping(target = "screenName", source = "screen.name")
    @Mapping(target = "cinemaId", source = "screen.cinema.id")
    @Mapping(target = "cinemaName", source = "screen.cinema.name")
    ShowtimeDTO toShowtimeDTO(Showtime showtime);

    @Mapping(target = "screenId", source = "seat.screen.id")
    @Mapping(target = "isBooked", source = "isBooked")
    SeatDTO toSeatDTO(Seat seat, Boolean isBooked);
}
