package ru.practicum.ewm.event.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.location.model.dto.LocationDto;
import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;

import java.time.format.DateTimeFormatter;

/**
 * Mapper for Event
 */
@Mapper
public interface EventMapper {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator",
            source = "initiatorUser")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn",
            expression= "java(LocalDateTime.now())")
    @Mapping(target = "eventDate",
//            expression = "java( newEventDto.getEventDate())",
            source =  "newEventDto.eventDate",
            dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event newEventDtoToEvent (NewEventDto newEventDto, EwmUser initiatorUser);


    @Mapping(target = "category",
            source = "categoryDto")
    @Mapping(target = "initiator",
            source = "ewmShortUserDto")
    @Mapping(target = "location",
            source = "locationDto")
    EventFullDto eventToEventFullDto (Event event,
                                      CategoryDto categoryDto,
                                      EwmShortUserDto ewmShortUserDto,
                                      LocationDto locationDto);

}
