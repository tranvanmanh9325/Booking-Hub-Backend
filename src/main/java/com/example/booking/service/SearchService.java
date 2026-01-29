package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.mapper.HotelMapper;
import com.example.booking.mapper.MovieMapper;
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
    private final HotelMapper hotelMapper;
    private final MovieMapper movieMapper;
    private final ContentRepository contentRepository;

    public SearchService(HotelRepository hotelRepository, MovieRepository movieRepository,
            RoomRepository roomRepository, CinemaRepository cinemaRepository,
            ShowtimeRepository showtimeRepository, SeatRepository seatRepository,
            HotelBookingRepository hotelBookingRepository, BookingSeatRepository bookingSeatRepository,
            ReviewService reviewService, HotelMapper hotelMapper, MovieMapper movieMapper,
            ContentRepository contentRepository) {
        this.hotelRepository = hotelRepository;
        this.movieRepository = movieRepository;
        this.roomRepository = roomRepository;
        this.cinemaRepository = cinemaRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.hotelBookingRepository = hotelBookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.reviewService = reviewService;
        this.hotelMapper = hotelMapper;
        this.movieMapper = movieMapper;
        this.contentRepository = contentRepository;
    }

    // Hotel Search Methods

    @org.springframework.cache.annotation.Cacheable(value = "hotels", key = "#page + '-' + #size")
    public Page<HotelDTO> getAllHotels(int page, int size) {
        logger.info("Fetching hotels page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotelPage = hotelRepository.findAll(pageable);

        List<Long> hotelIds = hotelPage.getContent().stream().map(Hotel::getId).collect(Collectors.toList());
        Map<Long, Double> ratingMap = reviewService.getAverageRatingsByHotelIds(hotelIds);

        return hotelPage.map(hotel -> hotelMapper.toHotelDTO(hotel, ratingMap.get(hotel.getId())));
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
        return hotelMapper.toHotelDTO(hotel, rating);
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
                    RoomDTO dto = hotelMapper.toRoomDTO(room);
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
                .map(hotel -> hotelMapper.toHotelDTO(hotel, ratingMap.get(hotel.getId())))
                .collect(Collectors.toList());
    }

    // Movie Search Methods

    @org.springframework.cache.annotation.Cacheable(value = "movies", key = "#page + '-' + #size")
    public Page<MovieDTO> getAllMovies(int page, int size) {
        logger.info("Fetching movies page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable).map(movieMapper::toMovieDTO);
    }

    @org.springframework.cache.annotation.Cacheable(value = "movies", key = "#id")
    public MovieDTO getMovieById(Long id) {
        logger.debug("Fetching movie with id: {}", id);

        if (id < 0) {
            Long contentId = -id;
            Content content = contentRepository.findById(contentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie/Content not found"));
            return mapContentToMovieDTO(content);
        }

        Movie movie = movieRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    logger.error("Movie not found with id: {}", id);
                    return new ResourceNotFoundException("Movie not found");
                });
        return movieMapper.toMovieDTO(movie);
    }

    public List<MovieDTO> searchMovies(String query) {
        return movieRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(movieMapper::toMovieDTO)
                .collect(Collectors.toList());
    }

    public List<MovieDTO> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre).stream()
                .map(movieMapper::toMovieDTO)
                .collect(Collectors.toList());
    }

    public List<MovieDTO> getNowShowing() {
        List<MovieDTO> movies = movieRepository.findNowShowing().stream()
                .map(movieMapper::toMovieDTO)
                .collect(Collectors.toList());

        // Also fetch from Content repository (Movies/Products added via generic CMS)
        List<String> types = java.util.Arrays.asList("Movie", "Sản phẩm", "Phim", "Product");
        List<Content> contents = contentRepository.findByTypeInAndStatus(types, "active");

        List<MovieDTO> contentMovies = contents.stream()
                .map(this::mapContentToMovieDTO)
                .collect(Collectors.toList());

        movies.addAll(contentMovies);
        return movies;
    }

    private MovieDTO mapContentToMovieDTO(Content content) {
        MovieDTO dto = new MovieDTO();
        // Use negative ID to avoid collision with real Movie IDs
        dto.setId(-content.getId());
        dto.setTitle(content.getName());
        dto.setDescription(content.getDescription());
        dto.setGenre(content.getType());
        dto.setDuration(content.getDuration() != null ? content.getDuration() : 120);
        dto.setRating(5.0);
        dto.setPosterUrl(content.getThumbnail());
        dto.setTrailerUrl(null);

        // Parse release date if possible, otherwise use Now
        if (content.getReleaseDate() != null) {
            try {
                // Assuming format yyyy-MM-dd
                String dateStr = content.getReleaseDate();
                if (dateStr.length() > 10)
                    dateStr = dateStr.substring(0, 10);
                dto.setReleaseDate(LocalDate.parse(dateStr).atStartOfDay());
            } catch (Exception e) {
                dto.setReleaseDate(java.time.LocalDateTime.now());
            }
        } else {
            dto.setReleaseDate(java.time.LocalDateTime.now());
        }

        return dto;
    }

    public List<CinemaDTO> getAllCinemas() {
        return cinemaRepository.findAll().stream()
                .map(movieMapper::toCinemaDTO)
                .collect(Collectors.toList());
    }

    public CinemaDTO getCinemaById(Long id) {
        Cinema cinema = cinemaRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Cinema not found"));
        return movieMapper.toCinemaDTO(cinema);
    }

    public List<ShowtimeDTO> getShowtimesByMovie(Long movieId) {
        if (movieId < 0) {
            // Generate mock showtimes for Content Movies
            List<ShowtimeDTO> showtimes = new java.util.ArrayList<>();
            LocalDate today = LocalDate.now();

            // Generate showtimes for next 7 days
            for (int i = 0; i < 7; i++) {
                LocalDate date = today.plusDays(i);

                // Show 1: 19:30
                ShowtimeDTO s1 = new ShowtimeDTO();
                s1.setId((long) -(i * 100 + 1)); // Mock ID: -1, -101, etc.
                s1.setMovieId(movieId);
                // We'd need to fetch title ideally, but for now leave blank or fetch
                // lightweight
                // But frontend usually has movie details already.
                s1.setScreenId(1L);
                s1.setScreenName("Rạp 3");
                s1.setCinemaId(1L);
                s1.setCinemaName("Booking Hub Center");
                s1.setStartTime(date.atTime(19, 30));
                s1.setEndTime(date.atTime(21, 30));
                s1.setPrice(85000.0);
                showtimes.add(s1);

                // Show 2: 22:00
                ShowtimeDTO s2 = new ShowtimeDTO();
                s2.setId((long) -(i * 100 + 2));
                s2.setMovieId(movieId);
                s2.setScreenId(1L);
                s2.setScreenName("Rạp 3");
                s2.setCinemaId(1L);
                s2.setCinemaName("Booking Hub Center");
                s2.setStartTime(date.atTime(22, 0));
                s2.setEndTime(date.atTime(0, 0));
                s2.setPrice(95000.0);
                showtimes.add(s2);
            }
            return showtimes;
        }

        return showtimeRepository.findByMovieId(movieId).stream()
                .map(movieMapper::toShowtimeDTO)
                .collect(Collectors.toList());
    }

    public List<SeatDTO> getSeatsByScreen(Long screenId, Long showtimeId) {
        List<Seat> seats = seatRepository.findByScreenId(screenId);
        List<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIdsByShowtimeId(showtimeId);

        return seats.stream()
                .map(seat -> movieMapper.toSeatDTO(seat, bookedSeatIds.contains(seat.getId())))
                .collect(Collectors.toList());
    }
}