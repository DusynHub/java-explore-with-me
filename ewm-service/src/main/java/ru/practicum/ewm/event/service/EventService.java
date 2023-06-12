package ru.practicum.ewm.event.service;

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
}
