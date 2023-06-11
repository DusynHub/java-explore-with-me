package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventMapper;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.model.dto.LocationDto;
import ru.practicum.ewm.location.model.dto.LocationMapper;
import ru.practicum.ewm.location.service.LocationService;
import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;
import ru.practicum.ewm.user.model.dto.EwmUserMapper;
import ru.practicum.ewm.user.service.EwmUserService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService{

    private final CategoryService categoryService;

    private final LocationService locationService;

    private final EventRepository eventRepository;

    private final EwmUserService ewmUserService;

    @Override
    @Transactional
    public EventFullDto postEvent(NewEventDto newEventDto, long initiator) {
        log.info("[Service] received a request to save new event");

        Location locationToSave = LocationMapper.INSTANCE.locationDtoToLocation(
                newEventDto.getLocation()
        );

        EwmUser initiatorUser = ewmUserService.getEwmUserEntityById(initiator);

        Event eventToSave = EventMapper.INSTANCE.newEventDtoToEvent(
                newEventDto,
                initiatorUser
        );

        Event savedEvent = eventRepository.save(eventToSave);

        CategoryDto categoryDto = categoryService.getCategory(newEventDto.getCategory());

        EwmShortUserDto ewmShortUserDto = EwmUserMapper.INSTANCE.ewmUserToEwmShortUserDto(
                initiatorUser
        );

        LocationDto locationDto = LocationMapper.INSTANCE.locationToLocationDto(
            locationService.saveLocation(locationToSave)
        );
//        todo добавить получение request
        return EventMapper.INSTANCE.eventToEventFullDto(
                savedEvent,
                categoryDto,
                ewmShortUserDto,
                locationDto
        );
    }
}
