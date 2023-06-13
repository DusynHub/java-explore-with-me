package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;

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

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> geEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false)  List<Long> categories,
            @RequestParam(defaultValue = "false")  boolean paid,
            @RequestParam(name = "rangeStart", required= false ) String rangeStartString,
            @RequestParam(name = "rangeEnd", required= false ) String rangeEndString,
            @RequestParam(defaultValue = "false")  boolean onlyAvailable,
            @RequestParam String Sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("[Public Event Controller] received a request GET /events");


        LocalDateTime rangeStart = null;
        if(rangeEndString != null) {
            String decodedRangeStart = URLDecoder.decode(rangeStartString, StandardCharsets.UTF_8);
            rangeStart = LocalDateTime.parse(decodedRangeStart, formatter);
        }

        LocalDateTime rangeEnd = null;
        if(rangeEndString != null){
            String decodedRangeEnd = URLDecoder.decode(rangeEndString, StandardCharsets.UTF_8);
            rangeEnd = LocalDateTime.parse(decodedRangeEnd, formatter);
        }

        return eventService.getEvents(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                Sort,
                from,
                size
        );
    }
}
