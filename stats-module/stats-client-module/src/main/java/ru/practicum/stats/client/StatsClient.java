package ru.practicum.stats.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.common.stats.dto.ViewStatsDto;

import java.util.List;

public interface StatsClient {

    ResponseEntity<List<ViewStatsDto>> getStat(String start, String end, List<String> uri, boolean unique);

    ResponseEntity<EndpointHitDto> postStat(String app, String uri, String ip, String timestamp);
}
