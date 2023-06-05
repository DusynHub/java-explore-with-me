package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.common.stats.dto.ViewStatsDto;
import ru.practicum.server.service.StatsServerService;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsServerController {

    private final StatsServerService statsServerService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public EndpointHitDto postStat(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("[StatsController] получен запрос POST /hit");
        return statsServerService.addEndPointHit(endpointHitDto);
    }

    @SneakyThrows
    @GetMapping("/stats")
    public List<ViewStatsDto> getStat(@RequestParam(name = "start") String start,
                                      @RequestParam(name = "end") String end,
                                      @RequestParam(name = "uris", defaultValue = "") List<String> uris,
                                      @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        log.info("[StatsController] получен запрос GET /stats");

        String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);

        LocalDateTime startTime = LocalDateTime.parse(decodedStart, formatter);
        LocalDateTime endTime = LocalDateTime.parse(decodedEnd, formatter);
        return statsServerService.getEndpointHitsCount(startTime, endTime, uris, unique);
    }
}
