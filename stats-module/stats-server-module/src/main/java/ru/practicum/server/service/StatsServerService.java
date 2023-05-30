package ru.practicum.server.service;

import ru.practicum.common.stats.dto.EndpointHitDto;

public interface StatsServerService {

    EndpointHitDto addEndPointHit(EndpointHitDto endpointHitDto);
}
