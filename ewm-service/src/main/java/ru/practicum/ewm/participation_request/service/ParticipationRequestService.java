package ru.practicum.ewm.participation_request.service;

import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {


    /**
     * Method to post new participation request to event
     *
     * @param userId requester id
     * @param eventId event id
     * @return posted request
     */
    ParticipationRequestDto postRequest(long userId, long eventId);


    /**
     * Method to cancel participation request
     *
     * @param requesterId requester id
     * @param requestId participation request id
     * @return cancelled participation request
     */
    ParticipationRequestDto cancelParticipationRequest(long requesterId, long requestId);


    /**
     * Method to get user participation requests
     *
     * @param userId user id
     * @return required participation requests
     */
    List<ParticipationRequestDto> getUserParticipationRequests(long userId);
}
