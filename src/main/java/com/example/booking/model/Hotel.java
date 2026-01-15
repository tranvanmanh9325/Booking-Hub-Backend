package com.example.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column
    private String city;

    @Column(name = "star_rating")
    private Integer starRating;

    @Column(length = 2000)
    private String description;

    @Column(length = 1000)
    private String facilities; // Comma-separated or JSON

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Room> rooms;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Hotel() {
    }

    public Hotel(Long id, String name, String address, String city, Integer starRating, String description,
            String facilities, String phoneNumber, String email, LocalDateTime createdAt, LocalDateTime updatedAt,
            List<Room> rooms) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.starRating = starRating;
        this.description = description;
        this.facilities = facilities;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.rooms = rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Hotel hotel = (Hotel) o;
        return java.util.Objects.equals(id, hotel.id) &&
                java.util.Objects.equals(name, hotel.name) &&
                java.util.Objects.equals(address, hotel.address) &&
                java.util.Objects.equals(city, hotel.city) &&
                java.util.Objects.equals(starRating, hotel.starRating) &&
                java.util.Objects.equals(description, hotel.description) &&
                java.util.Objects.equals(facilities, hotel.facilities) &&
                java.util.Objects.equals(phoneNumber, hotel.phoneNumber) &&
                java.util.Objects.equals(email, hotel.email) &&
                java.util.Objects.equals(createdAt, hotel.createdAt) &&
                java.util.Objects.equals(updatedAt, hotel.updatedAt) &&
                java.util.Objects.equals(rooms, hotel.rooms);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, address, city, starRating, description, facilities, phoneNumber, email,
                createdAt, updatedAt, rooms);
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", starRating=" + starRating +
                ", description='" + description + '\'' +
                ", facilities='" + facilities + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", rooms=" + rooms +
                '}';
    }
}