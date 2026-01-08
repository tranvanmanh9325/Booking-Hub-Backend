package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.model.*;
import com.example.booking.repository.*;

import com.example.booking.exception.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional(readOnly = true)
public class MovieService {

    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final CinemaRepository cinemaRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final MovieBookingRepository movieBookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final UserRepository userRepository;

    private final EmailService emailService;

    public MovieService(MovieRepository movieRepository, CinemaRepository cinemaRepository,
            ShowtimeRepository showtimeRepository, SeatRepository seatRepository,
            MovieBookingRepository movieBookingRepository, BookingSeatRepository bookingSeatRepository,
            UserRepository userRepository, EmailService emailService) {
        this.movieRepository = movieRepository;
        this.cinemaRepository = cinemaRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.movieBookingRepository = movieBookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @org.springframework.cache.annotation.Cacheable(value = "movies", key = "#page + '-' + #size")
    public Page<MovieDTO> getAllMovies(int page, int size) {
        logger.info("Fetching movies page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable).map(this::convertToDTO);
    }

    @org.springframework.cache.annotation.Cacheable(value = "movies", key = "#id")
    public MovieDTO getMovieById(Long id) {
        logger.debug("Fetching movie with id: {}", id);
        Movie movie = movieRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    logger.error("Movie not found with id: {}", id);
                    return new ResourceNotFoundException("Movie not found");
                });
        return convertToDTO(movie);
    }

    public List<MovieDTO> searchMovies(String query) {
        return movieRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MovieDTO> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MovieDTO> getNowShowing() {
        return movieRepository.findNowShowing().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CinemaDTO> getAllCinemas() {
        return cinemaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CinemaDTO getCinemaById(Long id) {
        Cinema cinema = cinemaRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Cinema not found"));
        return convertToDTO(cinema);
    }

    public List<ShowtimeDTO> getShowtimesByMovie(Long movieId) {
        return showtimeRepository.findByMovieId(movieId).stream()
                .map(this::convertToShowtimeDTO)
                .collect(Collectors.toList());
    }

    public List<SeatDTO> getSeatsByScreen(Long screenId, Long showtimeId) {
        List<Seat> seats = seatRepository.findByScreenId(screenId);
        List<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIdsByShowtimeId(showtimeId);

        return seats.stream()
                .map(seat -> {
                    SeatDTO dto = convertToSeatDTO(seat);
                    dto.setIsBooked(bookedSeatIds.contains(seat.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public MovieBookingDTO bookMovie(Long userId, BookMovieRequest request) {
        logger.info("Processing movie booking for userId: {}, showtimeId: {}", userId, request.getShowtimeId());
        User user = userRepository.findById(java.util.Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Showtime showtime = showtimeRepository.findById(java.util.Objects.requireNonNull(request.getShowtimeId()))
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        // Check if seats are available
        List<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIdsByShowtimeId(request.getShowtimeId());
        for (Long seatId : request.getSeatIds()) {
            if (bookedSeatIds.contains(seatId)) {
                throw new ConflictException("Seat " + seatId + " is already booked");
            }
        }

        // Create booking
        MovieBooking booking = new MovieBooking();
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("PENDING");

        // Calculate total price
        double totalPrice = showtime.getPrice() * request.getSeatIds().size();
        booking.setTotalPrice(totalPrice);

        booking = movieBookingRepository.save(booking);

        try {
            emailService.sendMovieBookingConfirmation(user.getEmail(), user.getFullName(), booking);
        } catch (Exception e) {
            logger.error("Failed to send movie booking confirmation", e);
        }

        // Create booking seats
        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(java.util.Objects.requireNonNull(seatId))
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);
            bookingSeat.setPrice(showtime.getPrice());
            bookingSeatRepository.save(bookingSeat);
        }

        return convertToBookingDTO(booking);
    }

    public List<MovieBookingDTO> getUserBookings(Long userId) {
        return movieBookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToBookingDTO)
                .collect(Collectors.toList());
    }

    public MovieBookingDTO getBookingById(Long bookingId, Long userId) {
        MovieBooking booking = movieBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Unauthorized access to booking");
        }

        return convertToBookingDTO(booking);
    }

    @Transactional
    public MovieBookingDTO cancelBooking(Long bookingId, Long userId) {
        MovieBooking booking = movieBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Unauthorized access to booking");
        }

        if (!booking.getStatus().equals("PENDING") && !booking.getStatus().equals("CONFIRMED")) {
            throw new BadRequestException("Cannot cancel booking with status: " + booking.getStatus());
        }

        booking.setStatus("CANCELLED");
        booking = movieBookingRepository.save(booking);

        try {
            emailService.sendBookingCancellation(booking.getUser().getEmail(), booking.getUser().getFullName(),
                    booking.getId(), booking.getShowtime().getMovie().getTitle());
        } catch (Exception e) {
            logger.error("Failed to send movie cancellation email", e);
        }

        return convertToBookingDTO(booking);
    }

    // Helper methods
    private MovieDTO convertToDTO(Movie movie) {
        return new MovieDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getGenre(),
                movie.getDuration(),
                movie.getRating(),
                movie.getPosterUrl(),
                movie.getTrailerUrl(),
                movie.getReleaseDate());
    }

    private CinemaDTO convertToDTO(Cinema cinema) {
        return new CinemaDTO(
                cinema.getId(),
                cinema.getName(),
                cinema.getAddress(),
                cinema.getCity(),
                cinema.getFacilities(),
                cinema.getPhoneNumber());
    }

    private ShowtimeDTO convertToShowtimeDTO(Showtime showtime) {
        return new ShowtimeDTO(
                showtime.getId(),
                showtime.getMovie().getId(),
                showtime.getMovie().getTitle(),
                showtime.getScreen().getId(),
                showtime.getScreen().getName(),
                showtime.getScreen().getCinema().getId(),
                showtime.getScreen().getCinema().getName(),
                showtime.getStartTime(),
                showtime.getEndTime(),
                showtime.getPrice());
    }

    private SeatDTO convertToSeatDTO(Seat seat) {
        return new SeatDTO(
                seat.getId(),
                seat.getScreen().getId(),
                seat.getRow(),
                seat.getNumber(),
                seat.getSeatType(),
                false);
    }

    private MovieBookingDTO convertToBookingDTO(MovieBooking booking) {
        List<SeatDTO> seatDTOs = booking.getBookingSeats().stream()
                .map(bs -> convertToSeatDTO(bs.getSeat()))
                .collect(Collectors.toList());

        return new MovieBookingDTO(
                booking.getId(),
                booking.getUser().getId(),
                booking.getShowtime().getId(),
                booking.getShowtime().getMovie().getTitle(),
                booking.getShowtime().getScreen().getCinema().getName(),
                booking.getShowtime().getScreen().getName(),
                booking.getShowtime().getStartTime(),
                booking.getBookingDate(),
                booking.getStatus(),
                booking.getTotalPrice(),
                seatDTOs);
    }
}