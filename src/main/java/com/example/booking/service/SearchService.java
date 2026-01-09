package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.model.*;
import com.example.booking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final HotelRepository hotelRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final CinemaRepository cinemaRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final HotelBookingRepository hotelBookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ReviewService reviewService;

    public SearchService(HotelRepository hotelRepository, MovieRepository movieRepository,
            RoomRepository roomRepository, CinemaRepository cinemaRepository,
            ShowtimeRepository showtimeRepository, SeatRepository seatRepository,
            HotelBookingRepository hotelBookingRepository, BookingSeatRepository bookingSeatRepository,
            ReviewService reviewService) {
        this.hotelRepository = hotelRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.cinemaRepository = cinemaRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.hotelBookingRepository = hotelBookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.reviewService = reviewService;
    }

    // Hotel Search Methods

    @org.springframework.cache.annotation.Cacheable(value = "hotels", key = "#page + '-' + #size")
    public Page<HotelDTO> getAllHotels(int page, int size) {
        logger.info("Fetching hotels page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotelPage = hotelRepository.findAll(pageable);

        List<Long> hotelIds = hotelPage.getContent().stream().map(Hotel::getId).collect(Collectors.toList());
        Map<Long, Double> ratingMap = reviewService.getAverageRatingsByHotelIds(hotelIds);

        return hotelPage.map(hotel -> convertToHotelDTO(hotel, ratingMap.get(hotel.getId())));
    }

    @org.springframework.cache.annotation.Cacheable(value = "hotels", key = "#id")
    public HotelDTO getHotelById(Long id) {
        logger.debug("Fetching hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    logger.error("Hotel not found with id: {}", id);
                    return new ResourceNotFoundException("Hotel not found");
                });
        Double rating = reviewService.getHotelAverageRating(id);
        return convertToHotelDTO(hotel, rating);
    }

    public List<HotelDTO> searchHotels(String query) {
        List<Hotel> hotels = hotelRepository.findByNameContainingIgnoreCase(query);
        return convertHotelsWithRatings(hotels);
    }

    public List<HotelDTO> getHotelsByCity(String city) {
        List<Hotel> hotels = hotelRepository.findByCity(city);
        return convertHotelsWithRatings(hotels);
    }

    public List<RoomDTO> getRoomsByHotel(Long hotelId, LocalDate checkIn, LocalDate checkOut, Integer guests) {
        logger.info("Searching rooms for hotelId: {}, checkIn: {}, checkOut: {}, guests: {}", hotelId, checkIn,
                checkOut, guests);
        List<Room> rooms = roomRepository.findAvailableRoomsByHotelAndGuests(hotelId, guests);

        if (rooms.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        List<Long> roomIds = rooms.stream().map(Room::getId).collect(Collectors.toList());
        List<HotelBooking> conflictingBookings = hotelBookingRepository.findConflictingBookingsForRooms(
                roomIds, checkIn, checkOut);

        Set<Long> occupiedRoomIds = conflictingBookings.stream()
                .map(booking -> booking.getRoom().getId())
                .collect(Collectors.toSet());

        return rooms.stream()
                .map(room -> {
                    RoomDTO dto = convertToRoomDTO(room);
                    dto.setIsAvailable(!occupiedRoomIds.contains(room.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<HotelDTO> convertHotelsWithRatings(List<Hotel> hotels) {
        if (hotels.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<Long> hotelIds = hotels.stream().map(Hotel::getId).collect(Collectors.toList());
        Map<Long, Double> ratingMap = reviewService.getAverageRatingsByHotelIds(hotelIds);

        return hotels.stream()
                .map(hotel -> convertToHotelDTO(hotel, ratingMap.get(hotel.getId())))
                .collect(Collectors.toList());
    }

    // Movie Search Methods

    @org.springframework.cache.annotation.Cacheable(value = "movies", key = "#page + '-' + #size")
    public Page<MovieDTO> getAllMovies(int page, int size) {
        logger.info("Fetching movies page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable).map(this::convertToMovieDTO);
    }

    @org.springframework.cache.annotation.Cacheable(value = "movies", key = "#id")
    public MovieDTO getMovieById(Long id) {
        logger.debug("Fetching movie with id: {}", id);
        Movie movie = movieRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    logger.error("Movie not found with id: {}", id);
                    return new ResourceNotFoundException("Movie not found");
                });
        return convertToMovieDTO(movie);
    }

    public List<MovieDTO> searchMovies(String query) {
        return movieRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(this::convertToMovieDTO)
                .collect(Collectors.toList());
    }

    public List<MovieDTO> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre).stream()
                .map(this::convertToMovieDTO)
                .collect(Collectors.toList());
    }

    public List<MovieDTO> getNowShowing() {
        return movieRepository.findNowShowing().stream()
                .map(this::convertToMovieDTO)
                .collect(Collectors.toList());
    }

    public List<CinemaDTO> getAllCinemas() {
        return cinemaRepository.findAll().stream()
                .map(this::convertToCinemaDTO)
                .collect(Collectors.toList());
    }

    public CinemaDTO getCinemaById(Long id) {
        Cinema cinema = cinemaRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Cinema not found"));
        return convertToCinemaDTO(cinema);
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

    // Helper conversion methods

    private HotelDTO convertToHotelDTO(Hotel hotel, Double averageRating) {
        return new HotelDTO(
                hotel.getId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getCity(),
                hotel.getStarRating(),
                hotel.getDescription(),
                hotel.getFacilities(),
                hotel.getPhoneNumber(),
                hotel.getEmail(),
                averageRating);
    }

    private RoomDTO convertToRoomDTO(Room room) {
        List<String> imageUrls = room.getImages().stream()
                .map(RoomImage::getImageUrl)
                .collect(Collectors.toList());

        return new RoomDTO(
                room.getId(),
                room.getHotel().getId(),
                room.getRoomType(),
                room.getMaxGuests(),
                room.getPricePerNight(),
                room.getAmenities(),
                room.getRoomNumber(),
                imageUrls,
                true);
    }

    private MovieDTO convertToMovieDTO(Movie movie) {
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

    private CinemaDTO convertToCinemaDTO(Cinema cinema) {
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
}