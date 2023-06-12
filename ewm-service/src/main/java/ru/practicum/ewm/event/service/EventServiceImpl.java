package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.ewm.enums.StateAction;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventMapper;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.InvalidResourceException;
import ru.practicum.ewm.exception.ResourceNotAvailableException;
import ru.practicum.ewm.exception.ResourceNotFoundException;
import ru.practicum.ewm.location.model.dto.LocationDto;
import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;
import ru.practicum.ewm.user.model.dto.EwmUserMapper;
import ru.practicum.ewm.user.service.EwmUserService;
import ru.practicum.ewm.util.OffsetPageRequest;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final ObjectMapper objectMapper;

    private final CategoryService categoryService;

    private final EventRepository eventRepository;

    private final EwmUserService ewmUserService;

    private final StatsClient statsClient;


    @Override
    @Transactional
    public EventFullDto postEvent(NewEventDto newEventDto, long initiator) {
        log.info("[Event Service] received a request to save new event");


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
                newEventDto.getLocation(),
                1
        );
    }

    @Override
    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        log.info("[Event Service] received a request to get user events");
        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size);
        BooleanExpression byUserId = QEvent.event.initiator.id.eq(userId);
        List<Event> resultEvents = eventRepository.findAll(byUserId, pageRequest).getContent();
        return makeEvenShortDtoFromEventsList(resultEvents);
    }

    @Override
    public EventFullDto getUserEventById(long userId, long eventId) {
        log.info("[Event Service] received a request to get user event by id");
        Event requiredEvent = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Event with id = '%d' not found", eventId)
                        )
                );

        if (requiredEvent.getInitiator().getId() != userId) {
            throw new ResourceNotAvailableException(
                    String.format("User with id = '%d' is not initiator of event with id = '%d'", userId, eventId)
            );
        }
        return makeEventFullDtoFromEvent(requiredEvent);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<String> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {
        log.info("[Event Service] received a request to get events by admin");
        BooleanExpression expression = Expressions.asBoolean(true).eq(true);

        if(users != null){
            expression = expression.and(QEvent.event.initiator.id.in(users));
        }

        if(states != null){
            expression = expression.and(QEvent.event.state.stringValue().in(states));
        }

        if(categories != null){
            expression = expression.and(QEvent.event.category.id.in(categories));
        }

        if(rangeStart != null){
            expression = expression.and(QEvent.event.eventDate.after(rangeStart));
        }

        if(rangeEnd != null){
            expression = expression.and(QEvent.event.eventDate.before(rangeEnd));
        }

        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size);
        List<Event> resultEvents = eventRepository.findAll(expression, pageRequest).getContent();
        return makeEventFullDtoFromEvents(resultEvents);
    }

    @Override
    public EventFullDto updateEventById(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {

        if(!eventRepository.existsById(eventId)){
            throw new ResourceNotFoundException(
                String.format("Event with id = '%d' not found", eventId)
            );
        }

        Event eventToUpdate = eventRepository.findById(eventId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                        String.format("Event with id = '%d' not found", eventId)
                )
            );

        if(eventToUpdate.getState() != State.PENDING){
            throw new InvalidResourceException(
                    "Cannot publish the event because it's not in the right state: PUBLISHED"
            );
        }

        try {
            State updatedState = State.getStateFromStateAction(
                    StateAction.getStateAction(updateEventAdminRequest.getStateAction())
            );
            eventToUpdate.setState(updatedState);
            objectMapper.updateValue(eventToUpdate, updateEventAdminRequest);


        } catch (JsonProcessingException e) {
            log.debug("JsonProcessingException при попытке сереализовать/десериализовать");
            throw new RuntimeException("Что-то пошло не так");
        }

        System.out.println(eventToUpdate);

        return null;
    }

    private List<EventFullDto> makeEventFullDtoFromEvents(List<Event> events) {

//        todo получение подтверждённых запросов на участие и просмотров

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
                                LocationDto.builder().lat(event.getLat()).lon(event.getLon()).build(),
                        1
                                )).collect(Collectors.toList());
    }

    private EventFullDto makeEventFullDtoFromEvent(Event singleEvent) {

//        todo получение подтверждённых запросов на участие и просмотров

        CategoryDto categoryDto = categoryService.getCategory(singleEvent.getCategory().getId());
        EwmShortUserDto ewmShortUserDto = EwmUserMapper.INSTANCE.ewmUserToEwmShortUserDto(
                ewmUserService.getEwmUserEntityById(singleEvent.getInitiator().getId())
        );

        return EventMapper.INSTANCE.eventToEventFullDto(singleEvent,
                categoryDto,
                ewmShortUserDto,
                LocationDto.builder()
                        .lat(singleEvent.getLat())
                        .lon(singleEvent.getLon()).build(),
                1);
    }


    private List<EventShortDto> makeEvenShortDtoFromEventsList(List<Event> events) {

//        todo получение подтверждённых запросов на участие и просмотров
        List<Long> categoryIds = events.stream().map((event) -> event.getCategory().getId()).collect(Collectors.toList());
        Map<Long, CategoryDto> categoryDtos = categoryService.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

        List<Long> ewmUserIds = events.stream().map((event) -> event.getInitiator().getId()).collect(Collectors.toList());
        Map<Long, EwmShortUserDto> ewmUserDtos = ewmUserService.findAllBy(ewmUserIds)
                .stream()
                .collect(Collectors.toMap(EwmShortUserDto::getId, ewmShortUserDto -> ewmShortUserDto));

        return events.stream()
                .map(event -> EventMapper.INSTANCE.eventToEventShortDto(event,
                        categoryDtos.get(event.getCategory().getId()),
                        ewmUserDtos.get(event.getInitiator().getId()),
                        1
                )).collect(Collectors.toList());
    }
}
