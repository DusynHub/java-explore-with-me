package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.model.dto.UpdateEventRequest;
import ru.practicum.ewm.participation_request.model.dto.ParticipationChangeStatusRequest;
import ru.practicum.ewm.participation_request.model.dto.ParticipationChangeStatusResult;
import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Service to Events
 */
public interface EventService {


    /**
     * Method to save new event
     *
     * @param newEventDto new event
     * @return saved event
     */
    EventFullDto postEvent(NewEventDto newEventDto, long initiator);


    /**
     * Method to increase current participants amount
     *
     * @param number  amount to increase
     * @param eventId event id
     * @return number of changed rows
     */
    int increaseByNumberConfirmedByEventId(int number, long eventId);

    /**
     * Method to decrease current participants amount
     *
     * @param number  amount to decrease
     * @param eventId event id
     * @return number of changed rows
     */
    int decreaseByNumberConfirmedByEventId(int number, long eventId);


    /**
     * Method to get events posted by user
     *
     * @param userId initiator id
     * @param from   first event to return
     * @param size   page size
     * @return event list
     */
    List<EventShortDto> getUserEvents(long userId, int from, int size);

    /**
     * Method to get event posted by user
     *
     * @param userId  initiator id
     * @param eventId event id
     * @return event
     */
    EventFullDto getUserEventById(long userId, long eventId);


    /**
     * Method to get events by admin
     *
     * @param users      user ids
     * @param states     event states
     * @param categories categories ids
     * @param rangeStart date and time no earlier than which the event should occur
     * @param rangeEnd   date and time no later than which the event should occur
     * @param from       the number of events that need to be skipped to form the current set
     * @param size       number of events on the page
     * @return required events
     */
    List<EventFullDto> getEventsByAdmin(List<Long> users,
                                        List<String> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size);

    /**
     * Method to update event by id from admin
     *
     * @param eventId            event id
     * @param updateEventRequest updateEventAdminRequest with event fields need to update
     * @return updated event
     */
    EventFullDto updateEventByIdFromAdmin(long eventId, UpdateEventRequest updateEventRequest);

    /**
     * Method to update event by id from user
     *
     * @param userId event owner id
     * @param eventId event id
     * @param updateEventRequest updateEventUserRequest with event fields need to update
     * @return updated event
     */
    EventFullDto updateEventByIdFromUser(long userId, long eventId,
                                         UpdateEventRequest updateEventRequest);


    /**
     * Method to get events from users
     *
     * @param text          search condition in annotation and description
     * @param categories    categories ids
     * @param paid          should event be paid
     * @param rangeStart    start of date range
     * @param rangeEnd      end of date range
     * @param onlyAvailable should event be available
     * @param sort          sort condition
     * @param from          the number of events that need to be skipped to form the current set
     * @param size          number of events on the page
     * @return required events
     */
    List<EventShortDto> getEvents(
            String text,
            List<Long> categories,
            boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            String sort,
            int from,
            int size,
            String clientIp,
            String endpointPath);

    /**
     * Method to get event by id and send to stat service
     *
     * @param eventId      event id
     * @param clientIp     client ip
     * @param endpointPath path of endpoint
     * @return required event
     */
    EventFullDto getEventById(long eventId, String clientIp, String endpointPath);

    /**
     * Method to get event entity by id
     *
     * @param eventId event id
     * @return required event
     */
    Event getEventEntityByIdMandatory(long eventId);

    /**
     * Method to get event proxy by id
     *
     * @param eventId event id
     * @return required event proxy
     */
    Event getEventProxyById(long eventId);

    /**
     * Method to get participation requests in specific event from user
     *
     * @param userId  user id
     * @param eventId event id
     * @return participation requests in specific event from user
     */
    List<ParticipationRequestDto> getParticipationRequestsInEvent(long userId, long eventId);

    /**
     * Method to patch participation requests in specific event from user
     *
     * @param userId              user id
     * @param eventId             event id
     * @param changeStatusRequest new participation request info
     * @return participation requests in specific event from user
     */
    ParticipationChangeStatusResult patchParticipationRequestsStatusInEvent(
            long userId,
            long eventId,
            ParticipationChangeStatusRequest changeStatusRequest
    );

    /**
     * Method to gent event entities
     *
     * @param eventIds event ids
     * @return required events
     */
    List<Event> getEventsEntityByIds(Set<Long> eventIds);

    /**
     * Method to make from Event list EventShortDto list
     *
     * @param events Event list
     * @return EventShortDto list
     */
    List<EventShortDto> makeEvenShortDtoFromEventsList(List<Event> events);

}
