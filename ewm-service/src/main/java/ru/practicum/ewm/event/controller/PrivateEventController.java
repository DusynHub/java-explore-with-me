package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.NewEventDto;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users/{userIdString}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PrivateEventController {

    private final EventService eventService;

    @PostMapping
    public EventFullDto postEvent(
            @PathVariable String userIdString,
            @RequestBody @Valid NewEventDto newEventDto
            ){
        log.info("[Private Controller] received a request POST /users/{}/events", userIdString);
        long userId = Long.parseLong(userIdString);

        return eventService.postEvent(newEventDto, userId);
    }

}
