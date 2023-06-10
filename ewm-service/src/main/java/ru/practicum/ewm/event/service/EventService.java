package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;

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
}
