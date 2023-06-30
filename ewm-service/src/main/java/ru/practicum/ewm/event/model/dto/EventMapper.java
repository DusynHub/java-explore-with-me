package ru.practicum.ewm.event.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.location.model.dto.LocationDto;
import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;
import ru.practicum.ewm.util.DateTimeFormatProvider;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Mapper for Event
 */
@Mapper(componentModel = "spring",
        imports = LocalDateTime.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormatProvider.PATTERN);

    @Mapping(target = "views", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator",
            source = "initiatorUser")
    @Mapping(target = "lat",
            source = "newEventDto.location.lat")
    @Mapping(target = "lon",
            source = "newEventDto.location.lon")
    @Mapping(target = "category",
            source = "category")
    @Mapping(target = "createdOn",
            expression = "java(LocalDateTime.now())")
    @Mapping(target = "eventDate",
            source = "newEventDto.eventDate",
            dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "paid", source = "newEventDto.paid")
    Event newEventDtoToEvent(NewEventDto newEventDto,
                             LocationDto locationDto,
                             EwmUser initiatorUser,
                             Category category);


    @Mapping(target = "id",
            source = "event.id")
    @Mapping(target = "category",
            source = "categoryDto")
    @Mapping(target = "initiator",
            source = "ewmShortUserDto")
    @Mapping(target = "location",
            source = "locationDto")
    @Mapping(target = "confirmedRequests",
            source = "event.confirmedRequests")
    @Mapping(target = "views",
            source = "viewsStats")
    @Mapping(target = "paid",
            source = "event.paid")
    EventFullDto eventToEventFullDto(Event event,
                                     CategoryDto categoryDto,
                                     EwmShortUserDto ewmShortUserDto,
                                     LocationDto locationDto,
                                     Long viewsStats);

    @Mapping(target = "id",
            source = "event.id")
    @Mapping(target = "category",
            source = "categoryDto")
    @Mapping(target = "initiator",
            source = "initiator")
    @Mapping(target = "confirmedRequests",
            source = "confirmedRequests")
    @Mapping(target = "eventDate",
            expression = "java(event.getEventDate().format(formatter))")
    @Mapping(target = "views",
            source = "viewsStats")
    @Mapping(target = "paid",
            source = "event.paid")
    EventShortDto eventToEventShortDto(Event event,
                                       CategoryDto categoryDto,
                                       EwmShortUserDto initiator,
                                       Integer confirmedRequests,
                                       Long viewsStats);


    @Mapping(target = "views", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lat", source = "locationDto.lat")
    @Mapping(target = "lon", source = "locationDto.lon")
    @Mapping(conditionExpression = "java(categoryToUpdate != null)",
            target = "category",
            source = "categoryToUpdate")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "eventDate",
            source = "updateEventRequest.eventDate")
    @Mapping(conditionExpression = "java(updateEventRequest.getParticipantLimit() != null)",
            target = "participantLimit",
            source = "updateEventRequest.participantLimit")
    void updateEventAdminRequestToEvent(
            UpdateEventRequest updateEventRequest,
            Category categoryToUpdate,
            LocationDto locationDto,
            State state,
            @MappingTarget Event eventToUpdate);


    @Mapping(target = "views", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lat", source = "locationDto.lat")
    @Mapping(target = "lon", source = "locationDto.lon")
    @Mapping(conditionExpression = "java(categoryToUpdate != null)",
            target = "category",
            source = "categoryToUpdate")
    @Mapping(target = "paid",
            source = "updateEventUserRequest.paid")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "eventDate",
            source = "updateEventUserRequest.eventDate")
    @Mapping(conditionExpression = "java(updateEventUserRequest.getParticipantLimit() != null)",
            target = "participantLimit",
            source = "updateEventUserRequest.participantLimit")
    void updateEventUserRequestToEvent(
            UpdateEventRequest updateEventUserRequest,
            Category categoryToUpdate,
            LocationDto locationDto,
            State state,
            @MappingTarget Event eventToUpdate);
}
