package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.model.*;
import com.example.booking.repository.*;

import com.example.booking.exception.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional(readOnly = true)
public class HotelService {

    private static final Logger logger = LoggerFactory.getLogger(HotelService.class);

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final HotelBookingRepository hotelBookingRepository;
    private final HotelReviewRepository hotelReviewRepository;
    private final UserRepository userRepository;

    public HotelService(HotelRepository hotelRepository, RoomRepository roomRepository,
            HotelBookingRepository hotelBookingRepository, HotelReviewRepository hotelReviewRepository,
            UserRepository userRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.hotelBookingRepository = hotelBookingRepository;
        this.hotelReviewRepository = hotelReviewRepository;
        this.userRepository = userRepository;
    }

    @org.springframework.cache.annotation.Cacheable("hotels")
    public List<HotelDTO> getAllHotels() {
        logger.info("Fetching all hotels");
        List<Hotel> hotels = hotelRepository.findAll();
        List<HotelRatingDTO> ratings = hotelReviewRepository.getAllAverageRatings();
        java.util.Map<Long, Double> ratingMap = ratings.stream()
                .collect(Collectors.toMap(HotelRatingDTO::getHotelId, HotelRatingDTO::getAverageRating));

        return hotels.stream()
                .map(hotel -> convertToDTO(hotel, ratingMap.get(hotel.getId())))
                .collect(Collectors.toList());
    }

    @org.springframework.cache.annotation.Cacheable(value = "hotels", key = "#id")
    public HotelDTO getHotelById(Long id) {
        logger.debug("Fetching hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> {
                    logger.error("Hotel not found with id: {}", id);
                    return new ResourceNotFoundException("Hotel not found");
                });
        Double rating = hotelReviewRepository.getAverageRatingByHotelId(id);
        return convertToDTO(hotel, rating);
    }

    public List<HotelDTO> searchHotels(String query) {
        List<Hotel> hotels = hotelRepository.findByNameContainingIgnoreCase(query);
        return convertHotelsWithRatings(hotels);
    }

    public List<HotelDTO> getHotelsByCity(String city) {
        List<Hotel> hotels = hotelRepository.findByCity(city);
        return convertHotelsWithRatings(hotels);
    }

    private List<HotelDTO> convertHotelsWithRatings(List<Hotel> hotels) {
        if (hotels.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<Long> hotelIds = hotels.stream().map(Hotel::getId).collect(Collectors.toList());
        List<HotelRatingDTO> ratings = hotelReviewRepository.getAverageRatingsByHotelIds(hotelIds);
        java.util.Map<Long, Double> ratingMap = ratings.stream()
                .collect(Collectors.toMap(HotelRatingDTO::getHotelId, HotelRatingDTO::getAverageRating));

        return hotels.stream()
                .map(hotel -> convertToDTO(hotel, ratingMap.get(hotel.getId())))
                .collect(Collectors.toList());
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

        // Create a set of occupied room IDs
        java.util.Set<Long> occupiedRoomIds = conflictingBookings.stream()
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

    @Transactional
    public HotelBookingDTO bookHotel(Long userId, BookHotelRequest request) {
        logger.info("Processing hotel booking for userId: {}", userId);
        User user = userRepository.findById(java.util.Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Hotel hotel = hotelRepository.findById(java.util.Objects.requireNonNull(request.getHotelId()))
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        Room room = roomRepository.findById(java.util.Objects.requireNonNull(request.getRoomId()))
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        // Validate dates
        if (request.getCheckIn().isAfter(request.getCheckOut()) ||
                request.getCheckIn().isBefore(LocalDate.now())) {
            throw new BadRequestException("Invalid check-in/check-out dates");
        }

        // Check availability
        List<HotelBooking> conflictingBookings = hotelBookingRepository.findConflictingBookings(
                request.getRoomId(), request.getCheckIn(), request.getCheckOut());
        if (!conflictingBookings.isEmpty()) {
            throw new ConflictException("Room is not available for the selected dates");
        }

        // Check guests
        if (request.getGuests() > room.getMaxGuests()) {
            throw new BadRequestException("Number of guests exceeds room capacity");
        }

        // Calculate total price
        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        double totalPrice = room.getPricePerNight() * nights;

        // Create booking
        HotelBooking booking = new HotelBooking();
        booking.setUser(user);
        booking.setHotel(hotel);
        booking.setRoom(room);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setGuests(request.getGuests());
        booking.setTotalPrice(totalPrice);
        booking.setStatus("PENDING");

        booking = hotelBookingRepository.save(booking);

        return convertToBookingDTO(booking);
    }

    public List<HotelBookingDTO> getUserBookings(Long userId) {
        return hotelBookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToBookingDTO)
                .collect(Collectors.toList());
    }

    public HotelBookingDTO getBookingById(Long bookingId, Long userId) {
        HotelBooking booking = hotelBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Unauthorized access to booking");
        }

        return convertToBookingDTO(booking);
    }

    @Transactional
    public HotelBookingDTO cancelBooking(Long bookingId, Long userId) {
        HotelBooking booking = hotelBookingRepository.findById(java.util.Objects.requireNonNull(bookingId))
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Unauthorized access to booking");
        }

        if (!booking.getStatus().equals("PENDING") && !booking.getStatus().equals("CONFIRMED")) {
            throw new BadRequestException("Cannot cancel booking with status: " + booking.getStatus());
        }

        booking.setStatus("CANCELLED");
        booking = hotelBookingRepository.save(booking);

        return convertToBookingDTO(booking);
    }

    // Helper methods
    private HotelDTO convertToDTO(Hotel hotel, Double averageRating) {
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

    private HotelBookingDTO convertToBookingDTO(HotelBooking booking) {
        return new HotelBookingDTO(
                booking.getId(),
                booking.getUser().getId(),
                booking.getHotel().getId(),
                booking.getHotel().getName(),
                booking.getRoom().getId(),
                booking.getRoom().getRoomType(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getGuests(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt());
    }
}