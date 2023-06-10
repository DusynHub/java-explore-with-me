package ru.practicum.ewm.event.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.model.dto.NewCategoryDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.EwmUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Mapper for Event
 */
@Mapper
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "views", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "initiator",
            source = "initiatorUser")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn",
            expression= "java(LocalDateTime.now())")
    Event newEventDtoToEvent (NewEventDto newEventDto, EwmUser initiatorUser);

//    @Mapping(target = "id", ignore = true)
//    Category newCategoryDtoToCategory (NewCategoryDto newCategoryDto);

}
