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
public abstract class MovieMapper {

    public abstract MovieDTO toMovieDTO(Movie movie);

    public abstract CinemaDTO toCinemaDTO(Cinema cinema);

    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "movieTitle", source = "movie.title")
    @Mapping(target = "contentId", source = "content.id")
    @Mapping(target = "contentName", source = "content.name")
    @Mapping(target = "screenId", source = "screen.id")
    @Mapping(target = "screenName", source = "screen.name")
    @Mapping(target = "cinemaId", source = "screen.cinema.id")
    @Mapping(target = "cinemaName", source = "screen.cinema.name")
    @Mapping(target = "cinemaAddress", source = "screen.cinema.address")
    @Mapping(target = "repeatUntilDate", ignore = true)
    public abstract ShowtimeDTO toShowtimeDTO(Showtime showtime);

    @Mapping(target = "screenId", source = "seat.screen.id")
    @Mapping(target = "isBooked", source = "isBooked")
    public abstract SeatDTO toSeatDTO(Seat seat, Boolean isBooked);

    @Mapping(target = "cinemaId", source = "cinema.id")
    @Mapping(target = "cinemaName", source = "cinema.name")
    public abstract com.example.booking.dto.ScreenDTO toScreenDTO(com.example.booking.model.Screen screen);

    @Mapping(target = "screens", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract Cinema toCinema(CinemaDTO cinemaDTO);

    @Mapping(target = "cinema", source = "cinemaId", qualifiedByName = "mapCinemaIdToCinema")
    @Mapping(target = "seats", ignore = true)
    @Mapping(target = "showtimes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract com.example.booking.model.Screen toScreen(com.example.booking.dto.ScreenDTO screenDTO);

    @org.mapstruct.Named("mapCinemaIdToCinema")
    protected Cinema mapCinemaIdToCinema(Long cinemaId) {
        if (cinemaId == null) {
            return null;
        }
        Cinema cinema = new Cinema();
        cinema.setId(cinemaId);
        return cinema;
    }
}