package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CinemaDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String facilities;
    private String phoneNumber;

    public CinemaDTO() {
    }

    public CinemaDTO(Long id, String name, String address, String city, String facilities, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.facilities = facilities;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CinemaDTO cinemaDTO = (CinemaDTO) o;
        return java.util.Objects.equals(id, cinemaDTO.id) &&
                java.util.Objects.equals(name, cinemaDTO.name) &&
                java.util.Objects.equals(address, cinemaDTO.address) &&
                java.util.Objects.equals(city, cinemaDTO.city) &&
                java.util.Objects.equals(facilities, cinemaDTO.facilities) &&
                java.util.Objects.equals(phoneNumber, cinemaDTO.phoneNumber);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, address, city, facilities, phoneNumber);
    }

    @Override
    public String toString() {
        return "CinemaDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", facilities='" + facilities + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}