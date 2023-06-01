package ru.practicum.server.service;

import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.common.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServerService {

    EndpointHitDto addEndPointHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getEndpointHitsCount(LocalDateTime start,
                                                     LocalDateTime end,
                                                     List<String> endpoints,
                                                     boolean unique);
}
