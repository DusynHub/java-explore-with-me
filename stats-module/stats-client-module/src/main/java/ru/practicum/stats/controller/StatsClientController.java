package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.stats.client.StatsClient;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsClientController {

    private final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> postStat(@RequestBody @Valid EndpointHitDto endpointHitDto){

        return statsClient.postStat(endpointHitDto);
    }


}
