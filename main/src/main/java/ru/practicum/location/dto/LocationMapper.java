package ru.practicum.location.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.location.entity.Location;

@UtilityClass
public class LocationMapper {
//    public LocationDto toLocationDto(Location location) {
//        return LocationDto.builder()
//                .lat(location.getLat())
//                .lon(location.getLon())
//                .build();
//    }
//
//    public Location toLocation(NewLocationDto newLocationDto) {
//        return Location.builder()
//                .name(newLocationDto.getName())
//                .lat(newLocationDto.getLat())
//                .lon(newLocationDto.getLon())
//                .radius(newLocationDto.getRadius())
//                .build();
//    }
//
//    public LocationResponseDto toNewLocationtDto(Location location) {
//        return LocationResponseDto.builder()
//                .id(location.getId())
//                .name(location.getName())
//                .lat(location.getLat())
//                .lon(location.getLon())
//                .status(location.getStatus())
//                .radius(location.getRadius())
//                .build();
//    }
//
//    public LocationResponseDto toLocationResponseDto(Location location) {
//        return LocationResponseDto.builder()
//                .id(location.getId())
//                .name(location.getName())
//                .lat(location.getLat())
//                .lon(location.getLon())
//                .radius(location.getRadius())
//                .status(location.getStatus())
//                .build();
//    }

    public Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
