package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.exception.*;
import com.example.booking.mapper.BookingMapper;
import com.example.booking.model.*;
import com.example.booking.enums.BookingStatus;

import com.example.booking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý các nghiệp vụ đặt chỗ (Booking).
 * Bao gồm đặt phòng khách sạn và đặt vé xem phim.
 */
@Service
@Transactional
@SuppressWarnings("null")
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final HotelBookingRepository hotelBookingRepository;
    private final MovieBookingRepository movieBookingRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatRepository seatRepository;
    private final EmailService emailService;
    private final BookingMapper bookingMapper;

    public BookingService(HotelBookingRepository hotelBookingRepository, MovieBookingRepository movieBookingRepository,
            UserRepository userRepository, HotelRepository hotelRepository, RoomRepository roomRepository,
            ShowtimeRepository showtimeRepository, BookingSeatRepository bookingSeatRepository,
            SeatRepository seatRepository, EmailService emailService, BookingMapper bookingMapper) {
        this.hotelBookingRepository = hotelBookingRepository;
        this.movieBookingRepository = movieBookingRepository;
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.showtimeRepository = showtimeRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.seatRepository = seatRepository;
        this.emailService = emailService;
        this.bookingMapper = bookingMapper;
    }

    // Hotel Booking Methods

    /**
     * Tạo booking khách sạn mới.
     * Kiểm tra tính khả dụng của phòng, tính tổng tiền và lưu booking.
     * 
     * @param userId  ID người đặt
     * @param request Thông tin đặt phòng
     * @return HotelBookingDTO thông tin booking đã tạo
     */
    public HotelBookingDTO bookHotel(Long userId, BookHotelRequest request) {
        logger.info("Processing hotel booking for userId: {}", userId);
        User user = userRepository.findById(java.util.Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Hotel hotel = hotelRepository.findById(java.util.Objects.requireNonNull(request.getHotelId()))
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        Room room = roomRepository.findById(java.util.Objects.requireNonNull(request.getRoomId()))
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (request.getCheckIn().isAfter(request.getCheckOut()) ||
                request.getCheckIn().isBefore(LocalDate.now())) {
            throw new BadRequestException("Invalid check-in/check-out dates");
        }

        List<HotelBooking> conflictingBookings = hotelBookingRepository.findConflictingBookings(
                request.getRoomId(), request.getCheckIn(), request.getCheckOut());
        if (!conflictingBookings.isEmpty()) {
            throw new ConflictException("Room is not available for the selected dates");
        }

        if (request.getGuests() > room.getMaxGuests()) {
            throw new BadRequestException("Number of guests exceeds room capacity");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        double totalPrice = room.getPricePerNight() * nights;

        HotelBooking booking = new HotelBooking();
        booking.setUser(user);
        booking.setHotel(hotel);
        booking.setRoom(room);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setGuests(request.getGuests());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);

        booking = hotelBookingRepository.save(booking);

        try {
            emailService.sendHotelBookingConfirmation(user.getEmail(), user.getFullName(), booking);
        } catch (Exception e) {
            logger.error("Failed to send hotel booking confirmation", e);
        }

        return bookingMapper.toHotelBookingDTO(booking);
    }

    @Transactional(readOnly = true)
    public List<HotelBookingDTO> getUserHotelBookings(Long userId) {
        return hotelBookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(bookingMapper::toHotelBookingDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HotelBookingDTO getHotelBookingById(Long bookingId, Long userId) {
        HotelBooking booking = hotelBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Unauthorized access to booking");
        }

        return bookingMapper.toHotelBookingDTO(booking);
    }

    public HotelBookingDTO cancelHotelBooking(Long bookingId, Long userId) {
        HotelBooking booking = hotelBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Unauthorized access to booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Cannot cancel booking with status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = hotelBookingRepository.save(booking);

        try {
            emailService.sendBookingCancellation(booking.getUser().getEmail(), booking.getUser().getFullName(),
                    booking.getId(), booking.getHotel().getName());
        } catch (Exception e) {
            logger.error("Failed to send hotel cancellation email", e);
        }

        return bookingMapper.toHotelBookingDTO(booking);
    }

    // Movie Booking Methods

    /**
     * Tạo booking vé xem phim mới.
     * Kiểm tra ghế trống, tính tiền và lưu booking.
     * 
     * @param userId  ID người đặt
     * @param request Thông tin đặt vé
     * @return MovieBookingDTO thông tin booking đã tạo
     */
    public MovieBookingDTO bookMovie(Long userId, BookMovieRequest request) {
        logger.info("Processing movie booking for userId: {}, showtimeId: {}", userId, request.getShowtimeId());
        User user = userRepository.findById(java.util.Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Showtime showtime = showtimeRepository.findById(java.util.Objects.requireNonNull(request.getShowtimeId()))
                .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

        List<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIdsByShowtimeId(request.getShowtimeId());
        for (Long seatId : request.getSeatIds()) {
            if (bookedSeatIds.contains(seatId)) {
                throw new ConflictException("Seat " + seatId + " is already booked");
            }
        }

        MovieBooking booking = new MovieBooking();
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);

        double totalPrice = showtime.getPrice() * request.getSeatIds().size();
        booking.setTotalPrice(totalPrice);

        booking = movieBookingRepository.save(booking);

        try {
            emailService.sendMovieBookingConfirmation(user.getEmail(), user.getFullName(), booking);
        } catch (Exception e) {
            logger.error("Failed to send movie booking confirmation", e);
        }

        for (Long seatId : request.getSeatIds()) {
            Seat seat = seatRepository.findById(java.util.Objects.requireNonNull(seatId))
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setSeat(seat);
            bookingSeat.setPrice(showtime.getPrice());
            bookingSeatRepository.save(bookingSeat);
        }

        // Refresh booking to get seats
        booking = movieBookingRepository.findById(booking.getId()).orElse(booking);

        return bookingMapper.toMovieBookingDTO(booking);
    }

    @Transactional(readOnly = true)
    public List<MovieBookingDTO> getUserMovieBookings(Long userId) {
        return movieBookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(bookingMapper::toMovieBookingDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovieBookingDTO getMovieBookingById(Long bookingId, Long userId) {
        MovieBooking booking = movieBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Unauthorized access to booking");
        }

        return bookingMapper.toMovieBookingDTO(booking);
    }

    public MovieBookingDTO cancelMovieBooking(Long bookingId, Long userId) {
        MovieBooking booking = movieBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Unauthorized access to booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Cannot cancel booking with status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = movieBookingRepository.save(booking);

        try {
            emailService.sendBookingCancellation(booking.getUser().getEmail(), booking.getUser().getFullName(),
                    booking.getId(), booking.getShowtime().getMovie().getTitle());
        } catch (Exception e) {
            logger.error("Failed to send movie cancellation email", e);
        }

        return bookingMapper.toMovieBookingDTO(booking);
    }
}