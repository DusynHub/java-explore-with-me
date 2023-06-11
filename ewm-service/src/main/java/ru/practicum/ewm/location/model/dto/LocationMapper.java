package ru.practicum.ewm.location.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.location.model.Location;

@Mapper
public interface LocationMapper {

    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    Location locationDtoToLocation(LocationDto locationDto);

    LocationDto locationToLocationDto(Location location);

}
