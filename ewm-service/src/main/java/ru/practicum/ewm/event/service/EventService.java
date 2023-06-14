package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.model.dto.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

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
    EventFullDto postEvent (NewEventDto newEventDto, long initiator);


    /**
     * Method to increase current participants amount
     *
     * @param number amount to increase
     * @param eventId event id
     * @return number of changed rows
     */
    int increaseByNumberCurrentParticipantsAmountByEventId (int number, long eventId);

    /**
     * Method to decrease current participants amount
     *
     * @param number amount to decrease
     * @param eventId event id
     * @return number of changed rows
     */
     int decreaseByNumberCurrentParticipantsAmountByEventId(int number, long eventId);


    /**
     * Method to get events posted by user
     *
     * @param userId initiator id
     * @param from first event to return
     * @param size page size
     * @return event list
     */
    List<EventShortDto> getUserEvents(long userId , int from, int size);

    /**
     * Method to get event posted by user
     *
     * @param userId initiator id
     * @param eventId event id
     * @return event
     */
    EventFullDto getUserEventById(long userId , long eventId);


    /**
     * Method to get events by admin
     *
     * @param users user ids
     * @param states event states
     * @param categories categories ids
     * @param rangeStart date and time no earlier than which the event should occur
     * @param rangeEnd date and time no later than which the event should occur
     * @param from the number of events that need to be skipped to form the current set
     * @param size number of events on the page
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
     * @param eventId event id
     * @param updateEventAdminRequest  updateEventAdminRequest with event fields need to update
     * @return updated event
     */
    EventFullDto updateEventById(long eventId, UpdateEventAdminRequest updateEventAdminRequest);


    /**
     * Method to get events from users
     *
     * @param text search condition in annotation and description
     * @param categories categories ids
     * @param paid should event be paid
     * @param rangeStart start of date range
     * @param rangeEnd end of date range
     * @param onlyAvailable should event be available
     * @param Sort sort condition
     * @param from the number of events that need to be skipped to form the current set
     * @param size number of events on the page
     * @return required events
     */
    List<EventShortDto> getEvents(
                                    String text,
                                    List<Long> categories,
                                    boolean paid,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    boolean onlyAvailable,
                                    String Sort,
                                    int from,
                                    int size,
                                    String clientIp,
                                    String endpointPath);

    /**
     * Method to get event by id
     *
     * @param eventId event id
     * @param clientIp client ip
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
    Event getEventEntityById(long eventId);

}
