package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HotelDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Integer starRating;
    private String description;
    private String facilities;
    private String phoneNumber;
    private String email;
    private Double averageRating;

    public HotelDTO() {
    }

    public HotelDTO(Long id, String name, String address, String city, Integer starRating, String description,
            String facilities, String phoneNumber, String email, Double averageRating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.starRating = starRating;
        this.description = description;
        this.facilities = facilities;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.averageRating = averageRating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HotelDTO hotelDTO = (HotelDTO) o;
        return java.util.Objects.equals(id, hotelDTO.id) &&
                java.util.Objects.equals(name, hotelDTO.name) &&
                java.util.Objects.equals(address, hotelDTO.address) &&
                java.util.Objects.equals(city, hotelDTO.city) &&
                java.util.Objects.equals(starRating, hotelDTO.starRating) &&
                java.util.Objects.equals(description, hotelDTO.description) &&
                java.util.Objects.equals(facilities, hotelDTO.facilities) &&
                java.util.Objects.equals(phoneNumber, hotelDTO.phoneNumber) &&
                java.util.Objects.equals(email, hotelDTO.email) &&
                java.util.Objects.equals(averageRating, hotelDTO.averageRating);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, address, city, starRating, description, facilities, phoneNumber, email,
                averageRating);
    }

    @Override
    public String toString() {
        return "HotelDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", starRating=" + starRating +
                ", description='" + description + '\'' +
                ", facilities='" + facilities + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", averageRating=" + averageRating +
                '}';
    }
}