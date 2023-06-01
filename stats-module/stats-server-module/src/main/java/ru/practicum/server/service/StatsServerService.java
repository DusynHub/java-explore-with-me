package ru.practicum.server.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.common.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServerService {

    EndpointHitDto addEndPointHit(EndpointHitDto endpointHitDto);

//    List<ViewStatsDto> getNonUniqueEndpointHitsCount(LocalDateTime start,
//                                                     LocalDateTime end,
//                                                     List<String> endpoints);

    List<ViewStatsDto> getEndpointHitsCount(LocalDateTime start,
                                                     LocalDateTime end,
                                                     List<String> endpoints,
                                                     boolean unique);

//    @Transactional
//    List<ViewStatsDto> getUniqueEndpointHitsCount(LocalDateTime start,
//                                                  LocalDateTime end,
//                                                  List<String> endpoints);
}
