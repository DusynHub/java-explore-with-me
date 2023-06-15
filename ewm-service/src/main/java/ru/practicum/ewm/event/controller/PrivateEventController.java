package ru.practicum.ewm.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.model.dto.UpdateEventRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.participation_request.model.dto.ParticipationChangeStatusRequest;
import ru.practicum.ewm.participation_request.model.dto.ParticipationChangeStatusResult;
import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userIdString}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(
            @PathVariable String userIdString,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("[Private Event Controller] received a request POST /users/{}/events", userIdString);
        long userId = Long.parseLong(userIdString);

        return eventService.getUserEvents(userId, from, size);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(
            @PathVariable String userIdString,
            @RequestBody @Valid NewEventDto newEventDto
    ) throws JsonProcessingException {
        log.info("[Private Event Controller] received a request POST /users/{}/events", userIdString);
        long userId = Long.parseLong(userIdString);

        ObjectMapper mapper =  new ObjectMapper();
        mapper.findAndRegisterModules();
        EventFullDto ss = eventService.postEvent(newEventDto, userId);
        String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ss);
        System.out.println("postEvent" + s);

//        return eventService.postEvent(newEventDto, userId);
        return ss;
    }

    @GetMapping("/{eventIdString}")
    public EventFullDto getUserEventById(
            @PathVariable String userIdString,
            @PathVariable String eventIdString) {
        log.info("[Private Event Controller] received a request GET /users/{}/events/{}",
                userIdString,
                eventIdString);
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventIdString}")
    public EventFullDto patchUserEventById(
            @PathVariable String userIdString,
            @PathVariable String eventIdString,
            @RequestBody @Valid  UpdateEventRequest updateEventRequest) throws JsonProcessingException {
        log.info("[Private Event Controller] received a request PATCH /users/{}/events/{}",
                userIdString,
                eventIdString);
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);

        ObjectMapper mapper =  new ObjectMapper();
        mapper.findAndRegisterModules();




        EventFullDto ss = eventService.updateEventByIdFromUser(userId, eventId, updateEventRequest);
        String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ss);
        System.out.println("patchUserEventById" + s);

//        return eventService.updateEventByIdFromUser(userId, eventId, updateEventAdminRequest);
        return ss;
    }

    @GetMapping("/{eventIdString}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsInEvent(
            @PathVariable String userIdString,
            @PathVariable String eventIdString) {
        log.info("[Private Event Controller] received a request GET /users/{}/events/{}/requests",
                userIdString,
                eventIdString);
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);

        return eventService.getParticipationRequestsInEvent(userId, eventId);
    }

    @PatchMapping("/{eventIdString}/requests")
    public ParticipationChangeStatusResult patchParticipationRequestsInEvent(
            @PathVariable String userIdString,
            @PathVariable String eventIdString,
            @RequestBody @Valid  ParticipationChangeStatusRequest changeStatusRequest) throws JsonProcessingException {
        log.info("[Private Event Controller] received private request PATCH /users/{}/events/{}/requests",
                userIdString,
                eventIdString);
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);



        ObjectMapper mapper =  new ObjectMapper();
        mapper.findAndRegisterModules();
        String s1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(changeStatusRequest);
        System.out.println("patchParticipationRequestsInEvent" + s1);

        ParticipationChangeStatusResult ss = eventService.patchParticipationRequestsStatusInEvent(userId, eventId, changeStatusRequest);
        String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ss);
        System.out.println("patchParticipationRequestsInEvent" + s);
        return ss;
//        return eventService.patchParticipationRequestsStatusInEvent(userId, eventId, changeStatusRequest);
    }

}
