package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.model.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.EventService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminEventController {

    private final EventService eventService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping
    public List<EventFullDto> geEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false)  List<String> states,
            @RequestParam(required = false)  List<Long> categories,
            @RequestParam(name = "rangeStart", required= false ) String rangeStartString,
            @RequestParam(name = "rangeEnd", required= false ) String rangeEndString,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("[Admin Event Controller] received a request GET /admin/events");


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

        return eventService.getEventsByAdmin(users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size);
    }

    @PatchMapping("/{eventIdString}")
    public EventFullDto patchEventById(
            @PathVariable String eventIdString,
            @RequestBody UpdateEventAdminRequest updateEventAdminRequest){
        log.info("[Admin Event Controller] received a request PATCH /admin/events/{}", eventIdString);
        long eventId = Long.parseLong(eventIdString);

        return eventService.updateEventById(eventId, updateEventAdminRequest);
    }
}
