package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventMapper;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ResourceNotFoundException;
import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.user.repository.EwmUserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;

    private final EwmUserRepository ewmUserRepository;

    @Override
    public EventFullDto postEvent(NewEventDto newEventDto, long initiator) {
        log.info("[Service] received a request to save new event");

        EwmUser initiatorUser = ewmUserRepository.findById(initiator).orElseThrow(() ->
                new ResourceNotFoundException(
                        String.format("EwmUser с id ='%d' not found",initiator)
                )
        );

        Event eventToSave = EventMapper.INSTANCE.newEventDtoToEvent(
                newEventDto,
                initiatorUser
        );

        eventRepository.save(eventToSave);





        return null;
    }
}
