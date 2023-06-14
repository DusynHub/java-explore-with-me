package ru.practicum.ewm.event.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.location.model.dto.LocationDto;
import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Mapper for Event
 */
@Mapper(imports = LocalDateTime.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
public interface EventMapper {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "initiator",
            source = "initiatorUser")
    @Mapping(target = "lat",
            source = "newEventDto.location.lat")
    @Mapping(target = "lon",
            source = "newEventDto.location.lon")
    @Mapping(target = "category",
            source = "category")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn",
            expression= "java(LocalDateTime.now())")
    @Mapping(target = "eventDate",
            source =  "newEventDto.eventDate",
            dateFormat = "yyyy-MM-dd HH:mm:ss")
    Event newEventDtoToEvent (NewEventDto newEventDto,
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
            source = "event.currentParticipantsAmount")
    @Mapping(target = "views",
            source = "viewsStats")
    EventFullDto eventToEventFullDto (Event event,
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
    EventShortDto eventToEventShortDto(Event event,
                                       CategoryDto categoryDto,
                                       EwmShortUserDto initiator,
                                       Integer confirmedRequests,
                                       Long viewsStats);
//    todo написать получение подтверённых запросов на участие



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lat", source = "locationDto.lat")
    @Mapping(target = "lon", source = "locationDto.lon")
    @Mapping(conditionExpression = "java(updateEventAdminRequest.getCategory() != 0L || categoryToUpdate != null)",
            target="category",
            source="categoryToUpdate")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "eventDate",
            source =  "updateEventAdminRequest.eventDate",
            dateFormat = "yyyy-MM-dd HH:mm:ss")
    void updateEventAdminRequestToEvent(
            UpdateEventAdminRequest updateEventAdminRequest,
            Category categoryToUpdate,
            LocationDto locationDto,
            State state,
            @MappingTarget Event eventToUpdate);
}
