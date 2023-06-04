package ru.practicum.stats.client;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.common.stats.dto.ViewStatsDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsClient {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final WebClient webClient;

    public StatsServiceImpl(String baseUrl) {
        webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public ResponseEntity<List<ViewStatsDto>> getStat(String start, String end, List<String> uri, boolean unique) {

        return webClient.get()
                .uri(UriComponentsBuilder
                        .fromUriString("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uri", uri)
                        .queryParam("unique", unique)
                        .build().encode().toUriString())
                .retrieve()
                .toEntityList(ViewStatsDto.class)
                .block();
    }

    @Override
    public ResponseEntity<EndpointHitDto> postStat(String app, String uri, String ip, String timestamp) {

        String decodedDate = URLDecoder.decode(timestamp, StandardCharsets.UTF_8);

        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.parse(decodedDate, formatter))
                .build();

        return webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(endpointHitDto))
                .retrieve()
                .toEntity(EndpointHitDto.class)
                .block();
    }
}
