package com.example.booking.mapper;

import com.example.booking.dto.HotelDTO;
import com.example.booking.dto.RoomDTO;
import com.example.booking.model.Hotel;
import com.example.booking.model.Room;
import com.example.booking.model.RoomImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    @Mapping(target = "averageRating", source = "averageRating")
    HotelDTO toHotelDTO(Hotel hotel, Double averageRating);

    @Mapping(target = "hotelId", source = "room.hotel.id")
    @Mapping(target = "imageUrls", source = "images", qualifiedByName = "mapRoomImages")
    @Mapping(target = "isAvailable", ignore = true)
    RoomDTO toRoomDTO(Room room);

    @Named("mapRoomImages")
    default List<String> mapRoomImages(List<RoomImage> images) {
        if (images == null) {
            return java.util.Collections.emptyList();
        }
        return images.stream()
                .map(RoomImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
