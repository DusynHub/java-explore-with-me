package ru.practicum.ewm.participation_request.service;


import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.enums.Status;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.InvalidResourceException;
import ru.practicum.ewm.exception.ResourceNotFoundException;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;
import ru.practicum.ewm.participation_request.model.QParticipationRequest;
import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestMapper;
import ru.practicum.ewm.participation_request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.user.service.EwmUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ParticipationRequestImpl implements ParticipationRequestService {

    private final EwmUserService ewmUserService;

    private final EventService eventService;

    private final ParticipationRequestRepository participationRequestRepository;

    @Override
    @Transactional
    public ParticipationRequestDto postRequest(long userId, long eventId) {
        log.info("[Participation Request Service] received a private request to post request");

        Event targetEvent = eventService.getEventEntityById(eventId);
        long eventInitiatorId = targetEvent.getInitiator().getId();

        if (userId == eventInitiatorId) {
            throw new InvalidResourceException(
                    "Event initiator cannot send participation request as a requester"
            );
        }

        if (targetEvent.getState() != State.PUBLISHED) {
            throw new InvalidResourceException(
                    String.format("Event state must be 'PUBLISHED', but was '%s'", targetEvent.getState())
            );
        }

        int availableConfirmations = targetEvent.getParticipantLimit() - targetEvent.getConfirmedRequests();

        if (targetEvent.getParticipantLimit() != 0 && availableConfirmations <= 0) {
            throw new InvalidResourceException(
                    "Event participation limit reached maximum"
            );
        }

        Status status = Status.PENDING;

        if (!targetEvent.isRequestModeration() || targetEvent.getParticipantLimit() == 0) {
            status = Status.CONFIRMED;
            eventService.increaseByNumberConfirmedByEventId(1, eventId);
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

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(long requesterId, long requestId) {
        log.info("[Participation Request Service] received a private request to cancel request" +
                " with id='{}' from user with id='{}'", requestId, requesterId);

        ParticipationRequest participationRequestToCancel = participationRequestRepository
                .findById(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Participation request with" +
                                        " id = '%s' not found", requestId)));

        if (requesterId != participationRequestToCancel.getRequester().getId()) {
            throw new InvalidResourceException(
                    String.format("User with id ='%s' is not a requester of " +
                            "participation request with id = '%s'", requesterId, requestId));
        }

        if (participationRequestToCancel.getStatus() == Status.CONFIRMED) {
            eventService.decreaseByNumberConfirmedByEventId(1,
                    participationRequestToCancel.getEvent().getId());
        }

        participationRequestToCancel.setStatus(Status.CANCELED);

        return ParticipationRequestMapper.INSTANCE.participationRequestToParticipationRequestDto(
                participationRequestRepository.save(participationRequestToCancel)
        );
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getUserParticipationRequests(long userId) {
        log.info("[Participation Request Service] " +
                "received a request to get participation requests of user with id ='{}'", userId);

        BooleanExpression findByRequesterId = QParticipationRequest.participationRequest.requester.id.eq(userId);
        Iterable<ParticipationRequest> iterable = participationRequestRepository.findAll(findByRequesterId);
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(ParticipationRequestMapper.INSTANCE::participationRequestToParticipationRequestDto)
                .collect(Collectors.toList());
    }
}
