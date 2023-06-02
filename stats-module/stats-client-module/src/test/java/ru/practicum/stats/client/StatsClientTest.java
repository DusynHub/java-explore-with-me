package ru.practicum.stats.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.common.stats.dto.EndpointHitDto;
import org.mockserver.model.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ExtendWith(SpringExtension.class)
@RequiredArgsConstructor
class StatsClientTest {

    private final String protocol = "http://";
    private final String host = "localhost";
    private final int port = 9090;

    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    private final StatsClient statsClient = new StatsClient(
            new StringBuilder()
                    .append(protocol)
                    .append(host)
                    .append(":")
                    .append(port).toString(),
            restTemplateBuilder);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ObjectMapper mapper = new ObjectMapper();

    private final MockServerClient mockServerClient = new MockServerClient(host, port);

    @Test
    void whenGetStats_thenReturnOk() throws JsonProcessingException {
        String startDate = "2022-09-06 12:00:54";
        String endDate = "2022-02-07 11:11:55";
        List<String> uriL = List.of("/event");
        boolean uniques = false;

        ClientAndServer.startClientAndServer(7070);

        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/stats")
                                .withQueryStringParameter("start", "2022-09-06%2012:00:54")
                                .withQueryStringParameter("end", "2022-02-07%2011:11:55")
                                .withQueryStringParameter("uri", "/event")
                                .withQueryStringParameter("unique", "false")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(mapper.writeValueAsString("Some stats"))
                );

        ResponseEntity<Object> response = statsClient.getStat(startDate, endDate, uriL, uniques);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Some stats");
    }

    @Test
    void postStats() throws JsonProcessingException {
        EndpointHitDto endpointHit = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events/2")
                .ip("192.163.0.1")
                .timestamp(LocalDateTime.parse("2022-09-06 11:00:23", formatter))
                .build();

        ClientAndServer.startClientAndServer(port);

        mockServerClient
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/hit")
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(mapper.writeValueAsString("Some stats has been saved"))
                );

        ResponseEntity<Object> response = statsClient.postStat(endpointHit);
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo("Some stats has been saved");
    }
}