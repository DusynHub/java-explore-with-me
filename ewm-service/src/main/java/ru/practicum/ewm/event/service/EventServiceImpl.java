package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.common.stats.dto.ViewStatsDto;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ObjectMapper objectMapper;

    private final CategoryService categoryService;

    private final EventRepository eventRepository;

    private final EwmUserService ewmUserService;

    private final StatsClient statsClient;


    @Override
    @Transactional
    public EventFullDto postEvent(NewEventDto newEventDto, long initiator) {
        log.info("[Event Service] received a private request to save new event");

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
                1,
                0L
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
        log.info("[Event Service] received a private request to get user event by id");
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
        log.info("[Event Service] received an admin request to get events");
        BooleanExpression expression = Expressions.asBoolean(true).eq(true);

        if (users != null) {
            expression = expression.and(QEvent.event.initiator.id.in(users));
        }

        if (states != null) {
            expression = expression.and(QEvent.event.state.stringValue().in(states));
        }

        if (categories != null) {
            expression = expression.and(QEvent.event.category.id.in(categories));
        }

        if (rangeStart != null) {
            expression = expression.and(QEvent.event.eventDate.after(rangeStart));
        }

        if (rangeEnd != null) {
            expression = expression.and(QEvent.event.eventDate.before(rangeEnd));
        }

        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size);
        List<Event> resultEvents = eventRepository.findAll(expression, pageRequest).getContent();
        return makeEventFullDtoFromEvents(resultEvents);
    }

    @Override
    @Transactional
    public EventFullDto updateEventById(long eventId,
                                        UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("[Event Service] received an admin request to patch event by id = '{}'", eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(
                    String.format("Event with id = '%d' not found", eventId));}
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Event with id = '%d' not found", eventId)));

        if (eventToUpdate.getEventDate().minusHours(1).isBefore(LocalDateTime.now())) {
            throw new InvalidResourceException(
                    "The start date of the event to be changed must be no earlier than one hour from the publication date.");}

        if (eventToUpdate.getState() != State.PENDING) {
            throw new InvalidResourceException(
                    "Cannot publish the event because it's not in the right state: PUBLISHED or CANCELLED");}

        State updatedState = State.getStateFromStateAction(
                StateAction.getStateAction(updateEventAdminRequest.getStateAction()));

        if (updatedState == State.PUBLISHED) {
            eventToUpdate.setPublishedOn(LocalDateTime.now());}

        Category category = categoryService.getCategoryEntity(eventToUpdate.getCategory().getId());

        if (updateEventAdminRequest.getCategory() != eventToUpdate.getCategory().getId()
                && updateEventAdminRequest.getCategory() != 0) {
            category = categoryService.getCategoryEntity(updateEventAdminRequest.getCategory());}

        EventMapper.INSTANCE.updateEventAdminRequestToEvent(
                updateEventAdminRequest,
                category,
                updateEventAdminRequest.getLocation(),
                updatedState,
                eventToUpdate);

        Event updatedEvent = eventRepository.save(eventToUpdate);
        return makeEventFullDtoFromEvent(updatedEvent);
    }

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         boolean onlyAvailable,
                                         String sort,
                                         int from,
                                         int size,
                                         String clientIp,
                                         String endpointPath) {
        log.info("[Event Service] received a public request to get events");

        BooleanExpression expression = Expressions.asBoolean(true).eq(true);

        if (text != null) {
            expression = expression.and(QEvent.event.annotation.containsIgnoreCase(text))
                    .or(QEvent.event.description.containsIgnoreCase(text));
        }

        if (categories != null) {
            expression = expression.and(QEvent.event.category.id.in(categories));
        }

        if (rangeStart == null && rangeEnd == null) {
            expression = expression.and(QEvent.event.eventDate.after(LocalDateTime.now()));
        } else {
            expression = expression.and(QEvent.event.eventDate.after(rangeStart))
                    .and(QEvent.event.eventDate.before(rangeEnd));
        }

        if (onlyAvailable) {
            expression = expression.and(QEvent.event.participantLimit.goe(0));
        }

        Sort getEvetnsSort = Sort.by(Sort.Direction.ASC, "eventDate");
        if (sort.equals("VIEWS")) {
            getEvetnsSort = Sort.by(Sort.Direction.ASC, "views");
        }

        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size, getEvetnsSort);
        List<Event> resultEvents = eventRepository.findAll(expression, pageRequest).getContent();

        ResponseEntity<EndpointHitDto> endpointHitDtoResponseEntity = statsClient.postStat(
                "ewv-service",
                endpointPath,
                clientIp,
                LocalDateTime.now().format(formatter)
        );


        System.out.println("*********СОХРАНЕНИЕ**********");
        System.out.println("*********СОХРАНЕНИЕ**********");
        System.out.println("*********СОХРАНЕНИЕ**********");
        System.out.println("*********СОХРАНЕНИЕ**********");
        System.out.println("*********СОХРАНЕНИЕ**********");
        System.out.println("*********СОХРАНЕНИЕ**********");
        System.out.println(endpointHitDtoResponseEntity.getBody());

        ResponseEntity<List<ViewStatsDto>> viewStatsDto = statsClient.getStat(
                LocalDateTime.now().minusYears(50).format(formatter),
                LocalDateTime.now().plusYears(50).format(formatter),
                List.of(endpointPath),
                false
        );


        System.out.println("*********СТАТИСТИКА**********");
        System.out.println("*********СТАТИСТИКА**********");
        System.out.println("*********СТАТИСТИКА**********");
        System.out.println("*********СТАТИСТИКА**********");
        System.out.println("*********СТАТИСТИКА**********");
        System.out.println("*********СТАТИСТИКА**********");
        System.out.println("*********СТАТИСТИКА**********");
        System.out.println(viewStatsDto.getBody());

        return makeEvenShortDtoFromEventsList(resultEvents);
    }

    @Override
    public EventFullDto getEventById(long eventId, String clientIp, String endpointPath) {
        log.info("[Event Service] received a public request to get event by id");

        Event requiredEvent = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Event with id = '%d' not found", eventId))
                        );
        if(requiredEvent.getState() != State.PUBLISHED){
            throw new InvalidResourceException(
                    String.format("Event should be published, but state was '%s'", requiredEvent.getState())
            );
        }

        statsClient.postStat(
                "ewv-service",
                endpointPath,
                clientIp,
                LocalDateTime.now().format(formatter)
        );

        return makeEventFullDtoFromEvent(requiredEvent);

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

        List<String> endpoints = events.stream().map((event) ->
                new StringBuilder()
                        .append(" /events/")
                        .append(event.getId()).toString())
                .collect(Collectors.toList());

        List<ViewStatsDto> stats = statsClient.getStat(
            LocalDateTime.now().minusYears(50).format(formatter),
            LocalDateTime.now().plusYears(50).format(formatter),
                endpoints,
                false
        ).getBody();

        Map<String, Long> views = stats.stream()
                .collect(
                        Collectors.toMap(
                                ViewStatsDto::getUri,
                                ViewStatsDto::getHits
                        )
                );

        return events.stream()
                .map(event -> EventMapper.INSTANCE.eventToEventFullDto(event,
                        categoryDtos.get(event.getCategory().getId()),
                        ewmUserDtos.get(event.getInitiator().getId()),
                        LocationDto.builder().lat(event.getLat()).lon(event.getLon()).build(),
                        1,
                        views.get(new StringBuilder()
                                .append(" /events/")
                                .append(event.getId()).toString())
                )).collect(Collectors.toList());
    }

    private EventFullDto makeEventFullDtoFromEvent(Event singleEvent) {

//        todo получение подтверждённых запросов на участие и просмотров

        CategoryDto categoryDto = categoryService.getCategory(singleEvent.getCategory().getId());
        EwmShortUserDto ewmShortUserDto = EwmUserMapper.INSTANCE.ewmUserToEwmShortUserDto(
                ewmUserService.getEwmUserEntityById(singleEvent.getInitiator().getId())
        );

        List<ViewStatsDto> stats = statsClient.getStat(
                LocalDateTime.now().minusYears(50).format(formatter),
                LocalDateTime.now().plusYears(50).format(formatter),
                List.of(new StringBuilder().append("/event/").append(singleEvent.getId()).toString()),
                false
        ).getBody();

        Map<String, Long> views = stats.stream()
                .collect(
                        Collectors.toMap(
                                ViewStatsDto::getUri,
                                ViewStatsDto::getHits
                        )
                );

        return EventMapper.INSTANCE.eventToEventFullDto(singleEvent,
                categoryDto,
                ewmShortUserDto,
                LocationDto.builder()
                        .lat(singleEvent.getLat())
                        .lon(singleEvent.getLon()).build(),
                1,
                views.get(new StringBuilder()
                        .append(" /events/")
                        .append(singleEvent.getId()).toString()));
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


        List<String> endpoints = events.stream().map((event) ->
                        new StringBuilder()
                                .append(" /events/")
                                .append(event.getId()).toString())
                .collect(Collectors.toList());

        List<ViewStatsDto> stats = statsClient.getStat(
                LocalDateTime.now().minusYears(50).format(formatter),
                LocalDateTime.now().plusYears(50).format(formatter),
                endpoints,
                false
        ).getBody();

        Map<String, Long> views = stats.stream()
                .collect(
                        Collectors.toMap(
                                ViewStatsDto::getUri,
                                ViewStatsDto::getHits
                        )
                );


        return events.stream()
                .map(event -> EventMapper.INSTANCE.eventToEventShortDto(event,
                        categoryDtos.get(event.getCategory().getId()),
                        ewmUserDtos.get(event.getInitiator().getId()),
                        1,
                        views.get(new StringBuilder()
                                .append(" /events/")
                                .append(event.getId()).toString())
                )).collect(Collectors.toList());
    }
}
