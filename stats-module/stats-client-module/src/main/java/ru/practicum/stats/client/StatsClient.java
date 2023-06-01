package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.common.stats.dto.EndpointHitDto;

import java.util.List;

@Service
@Component
public class StatsClient extends BaseClient {

    private final static String statsServerUrl = "http://localhost:5050";

    @Autowired
    public StatsClient(String statsServerUrl,
                       RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(statsServerUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }
    
    public ResponseEntity<Object> postStat( EndpointHitDto endpointHitDto) {
        return post("/hit", endpointHitDto);
    }

    public ResponseEntity<Object> getStat(String start, String end, List<String> uri, boolean unique) {

        String res = UriComponentsBuilder
                .fromUriString("/")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uri",uri)
                .queryParam("unique", unique)
                .build().encode().toUriString();

        System.out.println(res);

        return get(res);
    }

}