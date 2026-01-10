package com.example.booking.mapper;

import com.example.booking.dto.HotelBookingDTO;
import com.example.booking.dto.MovieBookingDTO;
import com.example.booking.dto.SeatDTO;
import com.example.booking.model.BookingSeat;
import com.example.booking.model.HotelBooking;
import com.example.booking.model.MovieBooking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
// Booking Mapper
public abstract class BookingMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "hotelId", source = "hotel.id")
    @Mapping(target = "hotelName", source = "hotel.name")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomType", source = "room.roomType")
    public abstract HotelBookingDTO toHotelBookingDTO(HotelBooking booking);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "showtimeId", source = "showtime.id")
    @Mapping(target = "movieTitle", source = "showtime.movie.title")
    @Mapping(target = "cinemaName", source = "showtime.screen.cinema.name")
    @Mapping(target = "screenName", source = "showtime.screen.name")
    @Mapping(target = "showtimeStart", source = "showtime.startTime")
    @Mapping(target = "seats", source = "bookingSeats")
    public abstract MovieBookingDTO toMovieBookingDTO(MovieBooking booking);

    @Mapping(target = "id", source = "seat.id")
    @Mapping(target = "screenId", source = "seat.screen.id")
    @Mapping(target = "row", source = "seat.row")
    @Mapping(target = "number", source = "seat.number")
    @Mapping(target = "seatType", source = "seat.seatType")
    @Mapping(target = "isBooked", constant = "false")
    public abstract SeatDTO toSeatDTO(BookingSeat bookingSeat);
}
