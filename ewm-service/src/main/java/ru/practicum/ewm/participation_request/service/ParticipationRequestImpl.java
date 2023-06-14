package ru.practicum.ewm.participation_request.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.enums.Status;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.InvalidResourceException;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;
import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestMapper;
import ru.practicum.ewm.participation_request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.user.service.EwmUserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestImpl implements ParticipationRequestService {

    private final EwmUserService ewmUserService;

    private final EventService eventService;

    private final ParticipationRequestRepository participationRequestRepository;

    @Override
    public ParticipationRequestDto postRequest(long userId, long eventId) {
        log.info("[Participation Request Service] received a private request to post request");

        Event targetEvent = eventService.getEventEntityById(eventId);
        long eventInitiatorId = targetEvent.getInitiator().getId();

        if(userId == eventInitiatorId){
            throw new InvalidResourceException(
                "Event initiator cannot send participation request as a requester"
            );
        }

        if(targetEvent.getState() != State.PUBLISHED){
            throw new InvalidResourceException(
                String.format("Event state must be 'PUBLISHED', but was '%s'", targetEvent.getState())
            );
        }

        int res = targetEvent.getParticipantLimit() - targetEvent.getCurrentParticipantsAmount();

        if( targetEvent.getParticipantLimit() !=0 && res < 0){
            throw new InvalidResourceException(
                    "Event participation limit reached maximum"
            );
        }

        Status status = Status.PENDING;

        if(targetEvent.isRequestModeration()){
            status = Status.CONFIRMED;
            eventService.increaseByNumberCurrentParticipantsAmountByEventId(1, eventId);
        }

        ParticipationRequest requestToSave = ParticipationRequest.builder()
                .event(targetEvent)
                .requester(ewmUserService.getEwmUserProxyById(userId))
                .status(status)
                .created(LocalDateTime.now())
                .build();

        return ParticipationRequestMapper.INSTANCE.participationRequestToParticipationRequestDto(
                participationRequestRepository.save(requestToSave)
        );
    }
}
