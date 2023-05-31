package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping("/hit")
    public EndpointHitDto postStat(@RequestBody @Valid EndpointHitDto endpointHitDto){

        return statsServerService.addEndPointHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStat(@RequestParam(name = "start") String start,
                                      @RequestParam(name = "end") String end,
                                      @RequestParam(name = "uris", defaultValue ="") List<String> uris,
                                      @RequestParam(name = "unique", defaultValue = "false") boolean unique){


        LocalDateTime startTime = LocalDateTime.parse(start, formatter);

        LocalDateTime endTime = LocalDateTime.parse(start, formatter);


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

        if(unique){
            return statsServerService.getUniqueEndpointHitsCount(startTime, endTime, uris);
        } else {
            return statsServerService.getNonUniqueEndpointHitsCount(startTime, endTime, uris);
        }

//        return statsServerService.getNonUniqueEndpointHitsCount(startTime, endTime, uris);
    }


}
