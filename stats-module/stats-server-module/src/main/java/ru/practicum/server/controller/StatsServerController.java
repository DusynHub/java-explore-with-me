package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.common.stats.dto.ViewStatsDto;
import ru.practicum.server.service.StatsServerService;

import javax.validation.Valid;
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
    public EndpointHitDto postStat(@RequestBody @Valid EndpointHitDto endpointHitDto){
        log.info("[StatsController: получен запрос POST /hit]");
        return statsServerService.addEndPointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStat(@RequestParam(name = "start") String start,
                                      @RequestParam(name = "end") String end,
                                      @RequestParam(name = "uris", defaultValue ="") List<String> uris,
                                      @RequestParam(name = "unique", defaultValue = "false") boolean unique){


        LocalDateTime startTime = LocalDateTime.parse(start, formatter);

        LocalDateTime endTime = LocalDateTime.parse(start, formatter);

        return statsServerService.getEndpointHitsCount(startTime, endTime, uris, unique);


//        System.out.println("uris = " + uris);
//        System.out.println("unique = " + unique);
//
//        String startUri = URLEncoder.encode(start);
//
//        System.out.println("URLEncoder.encode(start) = " + URLEncoder.encode(start));
//        System.out.println("URLEncoder.encode(end) = " + URLEncoder.encode(end));
//
//        String uriComponents =  UriComponentsBuilder.newInstance().path("/stats")
//                .queryParam("start", start).queryParam("end", end).encode(StandardCharsets.UTF_8).build().toUriString();
//
//        System.out.println(uriComponents);



//        return statsServerService.getNonUniqueEndpointHitsCount(startTime, endTime, uris);
    }


}
