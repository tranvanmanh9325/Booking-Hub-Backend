package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.model.*;
import com.example.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {
    
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final HotelBookingRepository hotelBookingRepository;
    private final HotelReviewRepository hotelReviewRepository;
    private final UserRepository userRepository;
    
    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public HotelDTO getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        return convertToDTO(hotel);
    }
    
    public List<HotelDTO> searchHotels(String query) {
        return hotelRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<HotelDTO> getHotelsByCity(String city) {
        return hotelRepository.findByCity(city).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByHotel(Long hotelId, LocalDate checkIn, LocalDate checkOut, Integer guests) {
        List<Room> rooms = roomRepository.findAvailableRoomsByHotelAndGuests(hotelId, guests);
        
        return rooms.stream()
                .map(room -> {
                    RoomDTO dto = convertToRoomDTO(room);
                    // Check availability
                    List<HotelBooking> conflictingBookings = hotelBookingRepository.findConflictingBookings(
                            room.getId(), checkIn, checkOut);
                    dto.setIsAvailable(conflictingBookings.isEmpty());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public HotelBookingDTO bookHotel(Long userId, BookHotelRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        // Validate dates
        if (request.getCheckIn().isAfter(request.getCheckOut()) || 
            request.getCheckIn().isBefore(LocalDate.now())) {
            throw new RuntimeException("Invalid check-in/check-out dates");
        }
        
        // Check availability
        List<HotelBooking> conflictingBookings = hotelBookingRepository.findConflictingBookings(
                request.getRoomId(), request.getCheckIn(), request.getCheckOut());
        if (!conflictingBookings.isEmpty()) {
            throw new RuntimeException("Room is not available for the selected dates");
        }
        
        // Check guests
        if (request.getGuests() > room.getMaxGuests()) {
            throw new RuntimeException("Number of guests exceeds room capacity");
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
        HotelBooking booking = hotelBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to booking");
        }
        
        return convertToBookingDTO(booking);
    }
    
    @Transactional
    public HotelBookingDTO cancelBooking(Long bookingId, Long userId) {
        HotelBooking booking = hotelBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to booking");
        }
        
        if (!booking.getStatus().equals("PENDING") && !booking.getStatus().equals("CONFIRMED")) {
            throw new RuntimeException("Cannot cancel booking with status: " + booking.getStatus());
        }
        
        booking.setStatus("CANCELLED");
        booking = hotelBookingRepository.save(booking);
        
        return convertToBookingDTO(booking);
    }
    
    // Helper methods
    private HotelDTO convertToDTO(Hotel hotel) {
        Double averageRating = hotelReviewRepository.getAverageRatingByHotelId(hotel.getId());
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
                averageRating
        );
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
                true
        );
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
                booking.getCreatedAt()
        );
    }
}