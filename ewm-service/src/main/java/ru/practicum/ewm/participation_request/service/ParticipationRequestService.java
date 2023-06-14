package ru.practicum.ewm.participation_request.service;

import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestDto;

public interface ParticipationRequestService {


    /**
     * Method to post new participation request to event
     *
     */
    ParticipationRequestDto postRequest(long userId, long eventId);
}
