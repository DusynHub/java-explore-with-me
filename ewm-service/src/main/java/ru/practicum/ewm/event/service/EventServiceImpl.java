package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventMapper;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.location.model.dto.LocationDto;
import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;
import ru.practicum.ewm.user.model.dto.EwmUserDto;
import ru.practicum.ewm.user.model.dto.EwmUserMapper;
import ru.practicum.ewm.user.service.EwmUserService;
import ru.practicum.ewm.util.OffsetPageRequest;
import ru.practicum.stats.client.StatsClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final CategoryService categoryService;

    private final EventRepository eventRepository;

    private final EwmUserService ewmUserService;

    private final StatsClient statsClient;


    @Override
    @Transactional
    public EventFullDto postEvent(NewEventDto newEventDto, long initiator) {
        log.info("[Service] received a request to save new event");


        EwmUser initiatorUser = ewmUserService.getEwmUserEntityById(initiator);
        Category categoryInEvent = categoryService.getCategoryEntity(newEventDto.getCategory());
        Event eventToSave = EventMapper.INSTANCE.newEventDtoToEvent(
                newEventDto,
                initiatorUser,
                categoryInEvent
        );

        eventToSave.setState(State.PENDING);

        Event savedEvent = eventRepository.save(eventToSave);


        CategoryDto categoryDto = categoryService.getCategory(newEventDto.getCategory());
        EwmShortUserDto ewmShortUserDto = EwmUserMapper.INSTANCE.ewmUserToEwmShortUserDto(
                initiatorUser
        );


//        todo добавить получение request
        return EventMapper.INSTANCE.eventToEventFullDto(
                savedEvent,
                categoryDto,
                ewmShortUserDto,
                newEventDto.getLocation()
        );
    }

    @Override
    public List<EventFullDto> getEvents(long userId, int from, int size) {
        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size);
        BooleanExpression byUserId = QEvent.event.id.eq(userId);
        List<Event> resultEvents = eventRepository.findAll(byUserId, pageRequest).getContent();
        return makeEventFullDtoFromEventsList(resultEvents);
    }

    private List<EventFullDto> makeEventFullDtoFromEventsList(List<Event> events) {

        List<Long> categoryIds = events.stream().map((event) -> event.getCategory().getId()).collect(Collectors.toList());
        Map<Long, CategoryDto> categoryDtos = categoryService.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

        List<Long> ewmUserIds = events.stream().map((event) -> event.getInitiator().getId()).collect(Collectors.toList());
        Map<Long, EwmShortUserDto> ewmUserDtos = ewmUserService.findAllBy(ewmUserIds)
                .stream()
                .collect(Collectors.toMap(EwmShortUserDto::getId, ewmShortUserDto -> ewmShortUserDto));

        return events.stream()
                .map(event -> EventMapper.INSTANCE.eventToEventFullDto(event,
                                categoryDtos.get(event.getCategory().getId()),
                                ewmUserDtos.get(event.getInitiator().getId()),
                                LocationDto.builder().lat(event.getLat()).lon(event.getLon()).build()
                                )).collect(Collectors.toList());
    }
}
