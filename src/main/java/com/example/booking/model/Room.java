package com.example.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "room_type", nullable = false)
    private String roomType; // Single, Double, Suite, etc.

    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @Column(name = "price_per_night", nullable = false)
    private Double pricePerNight;

    @Column(length = 1000)
    private String amenities; // WiFi, TV, AC, etc.

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomImage> images;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HotelBooking> bookings;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Room() {
    }

    public Room(Long id, Hotel hotel, String roomType, Integer maxGuests, Double pricePerNight, String amenities,
            String roomNumber, LocalDateTime createdAt, LocalDateTime updatedAt, List<RoomImage> images,
            List<HotelBooking> bookings) {
        this.id = id;
        this.hotel = hotel;
        this.roomType = roomType;
        this.maxGuests = maxGuests;
        this.pricePerNight = pricePerNight;
        this.amenities = amenities;
        this.roomNumber = roomNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.images = images;
        this.bookings = bookings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Room room = (Room) o;
        return java.util.Objects.equals(id, room.id) &&
                java.util.Objects.equals(hotel, room.hotel) &&
                java.util.Objects.equals(roomType, room.roomType) &&
                java.util.Objects.equals(maxGuests, room.maxGuests) &&
                java.util.Objects.equals(pricePerNight, room.pricePerNight) &&
                java.util.Objects.equals(amenities, room.amenities) &&
                java.util.Objects.equals(roomNumber, room.roomNumber) &&
                java.util.Objects.equals(createdAt, room.createdAt) &&
                java.util.Objects.equals(updatedAt, room.updatedAt) &&
                java.util.Objects.equals(images, room.images) &&
                java.util.Objects.equals(bookings, room.bookings);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, hotel, roomType, maxGuests, pricePerNight, amenities, roomNumber, createdAt,
                updatedAt, images, bookings);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", hotel=" + hotel +
                ", roomType='" + roomType + '\'' +
                ", maxGuests=" + maxGuests +
                ", pricePerNight=" + pricePerNight +
                ", amenities='" + amenities + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", images=" + images +
                ", bookings=" + bookings +
                '}';
    }
}