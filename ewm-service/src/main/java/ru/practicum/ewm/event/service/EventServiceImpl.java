package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.common.stats.dto.ViewStatsDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.enums.StateAction;
import ru.practicum.ewm.enums.Status;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventMapper;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.model.dto.UpdateEventRequest;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.InvalidResourceException;
import ru.practicum.ewm.exception.ResourceNotAvailableException;
import ru.practicum.ewm.exception.ResourceNotFoundException;
import ru.practicum.ewm.location.model.dto.LocationDto;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;
import ru.practicum.ewm.participation_request.model.QParticipationRequest;
import ru.practicum.ewm.participation_request.model.dto.ParticipationChangeStatusRequest;
import ru.practicum.ewm.participation_request.model.dto.ParticipationChangeStatusResult;
import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestMapper;
import ru.practicum.ewm.participation_request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;
import ru.practicum.ewm.user.model.dto.EwmUserMapper;
import ru.practicum.ewm.user.service.EwmUserService;
import ru.practicum.ewm.util.OffsetPageRequest;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.client.StatsClientImpl;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ParticipationRequestRepository participationRequestRepository;

    private final CategoryService categoryService;

    private final EventRepository eventRepository;

    private final EwmUserService ewmUserService;

    private final StatsClient statsClient;


    @Override
    @Transactional
    public EventFullDto postEvent(NewEventDto newEventDto, long initiator) {
        log.info("[Event Service] received a private request to save new event");

        EwmUser initiatorUser = ewmUserService.getEwmUserEntityById(initiator);
//        Category categoryInEvent = categoryService.getCategoryEntity(newEventDto.getCategory());
        if(newEventDto.getRequestModeration() == null){
            newEventDto.setRequestModeration(true);
        }
        Event eventToSave = EventMapper.INSTANCE.newEventDtoToEvent(
                newEventDto,
                newEventDto.getLocation(),
                initiatorUser,
                categoryService.getCategoryProxyById(newEventDto.getCategory())
        );

        eventToSave.setState(State.PENDING);
        eventToSave.setConfirmedRequests(0);
        Event savedEvent = eventRepository.save(eventToSave);

        CategoryDto categoryDto = categoryService.getCategory(newEventDto.getCategory());
        EwmShortUserDto ewmShortUserDto = EwmUserMapper.INSTANCE.ewmUserToEwmShortUserDto(
                initiatorUser
        );



        System.out.println("newEventDto.getRequestModeration() = " + newEventDto.getRequestModeration());
        System.out.println("newEventDto.getRequestModeration() = " + newEventDto.getRequestModeration());
        System.out.println("newEventDto.getRequestModeration() = " + newEventDto.getRequestModeration());
        System.out.println("newEventDto.getRequestModeration() = " + newEventDto.getRequestModeration());
        System.out.println("newEventDto.getRequestModeration() = " + newEventDto.getRequestModeration());

        return EventMapper.INSTANCE.eventToEventFullDto(
                savedEvent,
                categoryDto,
                ewmShortUserDto,
                newEventDto.getLocation(),
                0L
        );
    }

    @Override
    public int increaseByNumberConfirmedByEventId(int number, long eventId) {
        log.info("[Event Service] received a request to increase event field 'currentParticipantsAmount'");
        return eventRepository.increaseByNumberConfirmedByEventId(number, eventId);
    }

    @Override
    public int decreaseByNumberConfirmedByEventId(int number, long eventId) {
        log.info("[Event Service] received a request to decrease event field 'currentParticipantsAmount'");
        return eventRepository.decreaseByNumberConfirmedByEventId(number, eventId);
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
        Event requiredEvent = getEventEntityById(eventId);

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

        if(rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)){
            throw new ValidationException(
                    "Start date must be before End"
            );
        }

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
    public EventFullDto updateEventByIdFromAdmin(long eventId,
                                                 UpdateEventRequest updateEventRequest) {
        log.info("[Event Service] received an admin request to patch event by id = '{}'", eventId);
        checkEventExistenceById(eventId);
        Event eventToUpdate = getEventEntityById(eventId);

        if(updateEventRequest.getEventDate() != null ){
            LocalDateTime newDate = updateEventRequest.getEventDate();
            if (newDate.minusHours(1).isBefore(LocalDateTime.now())) {
                throw new ValidationException(
                        "The start date of the event to be changed must be no earlier than one hour from the publication date.");
            }
        }


        if (eventToUpdate.getState() != null && eventToUpdate.getState() != State.PENDING) {
            throw new InvalidResourceException(
                    "Cannot publish the event because it's not in the right state: PUBLISHED or CANCELLED");
        }

        State updatedState = eventToUpdate.getState();
        if(updateEventRequest.getStateAction()!= null){
            updatedState = State.getStateFromStateAction(
                    StateAction.getStateAction(updateEventRequest.getStateAction()));
        }

        if (updatedState == State.PUBLISHED) {
            eventToUpdate.setPublishedOn(LocalDateTime.now());
        }

        Category category = categoryService.getCategoryEntity(eventToUpdate.getCategory().getId());

        if (updateEventRequest.getCategory() != null && updateEventRequest.getCategory() != eventToUpdate.getCategory().getId()
                && updateEventRequest.getCategory() != 0) {
            category = categoryService.getCategoryEntity(updateEventRequest.getCategory());
        }

        EventMapper.INSTANCE.updateEventAdminRequestToEvent(
                updateEventRequest,
                category,
                updateEventRequest.getLocation(),
                updatedState,
                eventToUpdate);

        Event updatedEvent = eventRepository.save(eventToUpdate);
        return makeEventFullDtoFromEvent(updatedEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByIdFromUser(long userId, long eventId,
                                                UpdateEventRequest updateEventRequest) {
        log.info("[Event Service] received an private request from user with id = '{}'to patch event by id = '{}'", userId, eventId);
        checkEventExistenceById(eventId);
        Event eventToUpdate = getEventEntityById(eventId);


        if(updateEventRequest.getEventDate() != null ){
            LocalDateTime newDate = updateEventRequest.getEventDate();
            if (newDate.minusHours(1).isBefore(LocalDateTime.now())) {
                throw new ValidationException(
                        "The start date of the event to be changed must be no earlier than one hour from the publication date.");
            }
        }


        if (eventToUpdate.getState() != null &&  eventToUpdate.getState() == State.PUBLISHED) {
            throw new InvalidResourceException(
                    "Cannot publish the event because it's not in the right state: PUBLISHED or CANCELLED");
        }

        if (eventToUpdate.getInitiator() != null && eventToUpdate.getInitiator().getId() != userId) {
            throw new InvalidResourceException(
                    String.format("Cannot patch event with id = '%s' from user with id = '%s'", eventId, userId)
            );
        }

        State updatedState = eventToUpdate.getState();
        if(updateEventRequest.getStateAction()!= null){
            updatedState = State.getStateFromStateAction(
                    StateAction.getStateAction(updateEventRequest.getStateAction()));
        }

        Category category = categoryService.getCategoryEntity(eventToUpdate.getCategory().getId());

        if (updateEventRequest.getCategory()  != null && updateEventRequest.getCategory() != eventToUpdate.getCategory().getId()
                && updateEventRequest.getCategory() != 0) {
            category = categoryService.getCategoryEntity(updateEventRequest.getCategory());
        }

        EventMapper.INSTANCE.updateEventUserRequestToEvent(
                updateEventRequest,
                category,
                updateEventRequest.getLocation(),
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

        if(rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)){
            throw new ValidationException(
                    "Start date must be before End"
            );
        }

        BooleanExpression expression = Expressions.asBoolean(true).eq(true);

        if (text != null) {
            expression = expression.and(QEvent.event.annotation.containsIgnoreCase(text))
                    .or(QEvent.event.description.containsIgnoreCase(text));
        }

        if (categories != null) {
            expression = expression.and(QEvent.event.category.id.in(categories));
        }

        expression = expression.and(QEvent.event.eventDate.after(Objects.requireNonNullElseGet(rangeStart, LocalDateTime::now)));

        if (rangeEnd != null) {
            expression = expression.and(QEvent.event.eventDate.before(rangeEnd));
        }

        if (onlyAvailable) {
            expression = expression.and(QEvent.event.participantLimit.goe(0));
        }

        Sort getEventsSort = Sort.by(Sort.Direction.ASC, "eventDate");
        if (sort.equals("VIEWS")) {
            getEventsSort = Sort.by(Sort.Direction.ASC, "views");
        }

        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size, getEventsSort);
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

        Event requiredEvent = getEventEntityById(eventId);

        if (requiredEvent.getState() != State.PUBLISHED) {
            throw new ResourceNotFoundException(
                    String.format("Event should be PUBLISHED, but state was '%s'", requiredEvent.getState())
            );
        }

        statsClient.postStat(
                "ewv-service",
                endpointPath,
                clientIp,
                LocalDateTime.now().format(formatter)
        );
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
        return makeEventFullDtoFromEvent(requiredEvent);

    }

    @Override
    public Event getEventEntityById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Event with id = '%d' not found", eventId)
                        )
                );
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsInEvent(long userId, long eventId) {
        log.info("[Event Service] received a private request to get participation" +
                        " requests to event with id ='{} from user with id = '{}'",
                eventId,
                userId);
        BooleanExpression byEvent = QParticipationRequest.participationRequest.event.id.eq(eventId);
        Iterable<ParticipationRequest> iterable = participationRequestRepository.findAll(byEvent);
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(ParticipationRequestMapper.INSTANCE::participationRequestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationChangeStatusResult patchParticipationRequestsStatusInEvent(
            long userId,
            long eventId,
            ParticipationChangeStatusRequest changeStatusRequest) {
        log.info("[Event Service] received private request PATCH /users/{}/events/{}/requests",
                userId,
                eventId);
        Event requiredEvent = getEventEntityById(eventId);
        Status newStatus = changeStatusRequest.getStatus();

        List<ParticipationRequest> pendingRequests = StreamSupport.stream(
                participationRequestRepository.findAllById(changeStatusRequest.getRequestIds()).spliterator(), false).collect(Collectors.toList());

        int availableConfirmations = requiredEvent.getParticipantLimit() - requiredEvent.getConfirmedRequests();
        boolean isRequestNeedModeration = requiredEvent.isRequestModeration();
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        int confirmedRequestsCount = 0;

        if (availableConfirmations <= 0) {
            throw new InvalidResourceException(
                    "Partition requests does not need conformation");
        }

        for (ParticipationRequest participationRequest : pendingRequests) {
            if (participationRequest.getStatus() != Status.PENDING) {
                throw new InvalidResourceException(
                        String.format("The participation request must have 'PENDING' but was '%s'", participationRequest.getStatus()));
            }

            if(newStatus == Status.REJECTED){
                participationRequest.setStatus(newStatus);
                rejectedRequests.add(ParticipationRequestMapper.INSTANCE.participationRequestToParticipationRequestDto(
                                participationRequest));
            }

            if(newStatus == Status.CONFIRMED && confirmedRequestsCount < availableConfirmations){
                participationRequest.setStatus(newStatus);
                confirmedRequestsCount++;
                confirmedRequests.add(ParticipationRequestMapper.INSTANCE.participationRequestToParticipationRequestDto(
                                participationRequest));
            } else {
                participationRequest.setStatus(Status.REJECTED);
                rejectedRequests.add(ParticipationRequestMapper.INSTANCE.participationRequestToParticipationRequestDto(
                                participationRequest));
            }
        }

        eventRepository.increaseByNumberConfirmedByEventId(confirmedRequestsCount, eventId);


//        Event requiredEvent = getEventEntityById(eventId);
//        BooleanExpression byPendingStatus = QParticipationRequest.participationRequest.status.eq(Status.PENDING);
//        Sort sortByCreatedDesc = Sort.by(Sort.Direction.DESC, "created");
//        List<ParticipationRequest> pendingRequests = StreamSupport.stream(
//                participationRequestRepository.findAll(byPendingStatus, sortByCreatedDesc).spliterator(), false
//        ).collect(Collectors.toList());
//
//
//        int availableConfirmations = requiredEvent.getParticipantLimit() - requiredEvent.getConfirmedRequests();
//        int requiredConformations = pendingRequests.size();
//        boolean canConfirmAllPendingRequests = availableConfirmations >= requiredConformations;
//        boolean isRequestNeedModeration = requiredEvent.isRequestModeration();
//        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>(0);
//        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>(0);;
//        List<Long> requestsIdsToConfirm;
//        List<Long> requestsIdsToCancel;
//
//        if (!isRequestNeedModeration || canConfirmAllPendingRequests) {
//            requestsIdsToConfirm = pendingRequests.stream()
//                    .map(ParticipationRequest::getId)
//                    .collect(Collectors.toList());
//            participationRequestRepository.changeParticipationRequests(Status.CONFIRMED, requestsIdsToConfirm);
//            eventRepository.increaseByNumberConfirmedByEventId(requestsIdsToConfirm.size(), eventId);
//            pendingRequests.forEach(requestToConfirm -> requestToConfirm.setStatus(Status.CONFIRMED));
//            confirmedRequests = pendingRequests.stream()
//                    .map(
//                            ParticipationRequestMapper.INSTANCE::participationRequestToParticipationRequestDto
//                    ).collect(Collectors.toList());
//            rejectedRequests = new ArrayList<>(0);
//        }
//
//        if(availableConfirmations == 0){
//            requestsIdsToConfirm = pendingRequests.stream()
//                    .map(ParticipationRequest::getId)
//                    .collect(Collectors.toList());
//            participationRequestRepository.changeParticipationRequests(Status.CANCELED, requestsIdsToConfirm);
//            pendingRequests.forEach(requestToConfirm -> requestToConfirm.setStatus(Status.CANCELED));
//            confirmedRequests = new ArrayList<>(0);
//            rejectedRequests = pendingRequests.stream()
//                    .map(
//                            ParticipationRequestMapper.INSTANCE::participationRequestToParticipationRequestDto
//                    ).collect(Collectors.toList());
//        }
//
//        if(!canConfirmAllPendingRequests){
//
//            List<ParticipationRequest> pendingToConfirmRequests = pendingRequests.subList(0,availableConfirmations);
//            requestsIdsToConfirm = pendingToConfirmRequests.stream()
//                    .map(ParticipationRequest::getId)
//                    .collect(Collectors.toList());
//            participationRequestRepository.changeParticipationRequests(Status.CONFIRMED, requestsIdsToConfirm);
//            pendingToConfirmRequests.forEach(requestToConfirm -> requestToConfirm.setStatus(Status.CANCELED));
//            confirmedRequests = pendingToConfirmRequests.stream()
//                    .map(
//                            ParticipationRequestMapper.INSTANCE::participationRequestToParticipationRequestDto
//                    ).collect(Collectors.toList());
//
//            List<ParticipationRequest> pendingToCancelRequests = pendingRequests.subList(2,requiredConformations);
//            requestsIdsToCancel = pendingToCancelRequests.stream()
//                    .map(ParticipationRequest::getId)
//                    .collect(Collectors.toList());
//            participationRequestRepository.changeParticipationRequests(Status.CANCELED, requestsIdsToCancel);
//            pendingToCancelRequests.forEach(requestToCancel-> requestToCancel.setStatus(Status.CANCELED));
//
//            rejectedRequests = pendingToCancelRequests.stream()
//                    .map(
//                            ParticipationRequestMapper.INSTANCE::participationRequestToParticipationRequestDto
//                    ).collect(Collectors.toList());
//        }
        return new ParticipationChangeStatusResult(confirmedRequests, rejectedRequests);
    }

    @Override
    public List<Event> getEventsById(Set<Long> eventIds) {
        log.info("[Event Service] received a request to get event entities by ids");
        return eventRepository.findAllById(eventIds);
    }

    @Override
    public List<EventShortDto> makeEvenShortDtoFromEventsList(List<Event> events) {

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
                                .append("/events/")
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
                        views.getOrDefault(new StringBuilder()
                                .append("/events/")
                                .append(event.getId()).toString(), 0L)
                )).collect(Collectors.toList());
    }

    private List<EventFullDto> makeEventFullDtoFromEvents(List<Event> events) {

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
                        views.get(new StringBuilder()
                                .append(" /events/")
                                .append(event.getId()).toString())
                )).collect(Collectors.toList());
    }

    private EventFullDto makeEventFullDtoFromEvent(Event singleEvent) {

        CategoryDto categoryDto = categoryService.getCategory(singleEvent.getCategory().getId());
        EwmShortUserDto ewmShortUserDto = EwmUserMapper.INSTANCE.ewmUserToEwmShortUserDto(
                ewmUserService.getEwmUserEntityById(singleEvent.getInitiator().getId())
        );

        List<ViewStatsDto> stats = statsClient.getStat(
                LocalDateTime.now().minusYears(50).format(formatter),
                LocalDateTime.now().plusYears(50).format(formatter),
                List.of(new StringBuilder().append("/events/").append(singleEvent.getId()).toString()),
                true
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
                views.get(new StringBuilder()
                        .append("/events/")
                        .append(singleEvent.getId()).toString()));
    }

    private void checkEventExistenceById(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(
                    String.format("Event with id = '%d' not found", eventId));
        }
    }
}
