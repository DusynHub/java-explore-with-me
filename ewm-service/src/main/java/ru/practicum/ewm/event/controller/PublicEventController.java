package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.util.DateTimeFormatProvider;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PublicEventController {


    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormatProvider.PATTERN);

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(defaultValue = "false") boolean paid,
            @RequestParam(name = "rangeStart", required = false) String rangeStartString,
            @RequestParam(name = "rangeEnd", required = false) String rangeEndString,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.info("[Public Event Controller] received a request GET /events");
        log.debug("client ip: {}", request.getRemoteAddr());
        log.debug("endpoint path: {}", request.getRequestURI());


        LocalDateTime rangeStart = getLocalDateTimeFromRequestParam(rangeStartString);
        LocalDateTime rangeEnd = getLocalDateTimeFromRequestParam(rangeEndString);

        return eventService.getEvents(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size,
                request.getRemoteAddr(),
                request.getRequestURI()
        );
    }

    @GetMapping("/{eventIdString}")
    public EventFullDto geEventById(
            @PathVariable String eventIdString,
            HttpServletRequest request) {
        log.info("[Public Event Controller] received a request GET /events/{}", eventIdString);
        log.debug("client ip: {}", request.getRemoteAddr());
        log.debug("endpoint path: {}", request.getRequestURI());
        long eventId = Long.parseLong(eventIdString);
        return eventService.getEventById(eventId, request.getRemoteAddr(), request.getRequestURI());
    }

    private LocalDateTime getLocalDateTimeFromRequestParam(String dateInString) {
        LocalDateTime localdateTime = null;
        if (dateInString != null) {
            String decodedRangeStart = URLDecoder.decode(dateInString, StandardCharsets.UTF_8);
            localdateTime = LocalDateTime.parse(decodedRangeStart, formatter);
        }
        return localdateTime;
    }
}

