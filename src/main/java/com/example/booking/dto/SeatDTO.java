package com.example.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SeatDTO {
    private Long id;
    private Long screenId;
    private String row;
    private Integer number;
    private String seatType;
    private Boolean isBooked;

    public SeatDTO() {
    }

    public SeatDTO(Long id, Long screenId, String row, Integer number, String seatType, Boolean isBooked) {
        this.id = id;
        this.screenId = screenId;
        this.row = row;
        this.number = number;
        this.seatType = seatType;
        this.isBooked = isBooked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SeatDTO seatDTO = (SeatDTO) o;
        return java.util.Objects.equals(id, seatDTO.id) &&
                java.util.Objects.equals(screenId, seatDTO.screenId) &&
                java.util.Objects.equals(row, seatDTO.row) &&
                java.util.Objects.equals(number, seatDTO.number) &&
                java.util.Objects.equals(seatType, seatDTO.seatType) &&
                java.util.Objects.equals(isBooked, seatDTO.isBooked);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, screenId, row, number, seatType, isBooked);
    }

    @Override
    public String toString() {
        return "SeatDTO{" +
                "id=" + id +
                ", screenId=" + screenId +
                ", row='" + row + '\'' +
                ", number=" + number +
                ", seatType='" + seatType + '\'' +
                ", isBooked=" + isBooked +
                '}';
    }
}