package ru.practicum.stats.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.common.stats.dto.EndpointHitDto;
import org.mockserver.model.MediaType;
import ru.practicum.common.stats.dto.ViewStatsDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ExtendWith(SpringExtension.class)
@RequiredArgsConstructor
class StatsClientTest {
    private final String protocol = "http";
    private final String host = "localhost";
    private final int port = 9090;

    private final String baseUrl = UriComponentsBuilder.newInstance().scheme(protocol).host(host)
            .port(port).toUriString();

    private final StatsClient statsClient = new StatsClientImpl(baseUrl);

    private final ObjectMapper mapper = new ObjectMapper();

    private final MockServerClient mockServerClient = new MockServerClient(host, port);

    @Test
    void whenGetStats_thenReturnOk() throws JsonProcessingException {
        String startDate = "2022-09-06 12:00:54";
        String endDate = "2022-02-07 11:11:55";
        List<String> uriL = List.of("/events");
        boolean uniques = false;

        mapper.findAndRegisterModules();

        ClientAndServer.startClientAndServer(port);

        mockServerClient
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/stats")
                                .withQueryStringParameter("start", "2022-09-06%2012:00:54")
                                .withQueryStringParameter("end", "2022-02-07%2011:11:55")
                                .withQueryStringParameter("uris", "/events")
                                .withQueryStringParameter("unique", "false")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(mapper.writeValueAsString(ViewStatsDto.builder().build()))
                );

        ResponseEntity<List<ViewStatsDto>> response = statsClient.getStat(startDate, endDate, uriL, uniques);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void postStats() throws JsonProcessingException {

        String app = "ewm-main-service";
        String uri = "/events/2";
        String ip = "192.163.0.1";
        String timestamp = "2022-09-06 11:00:23";

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
                                .withBody(mapper.writeValueAsString(ViewStatsDto.builder().build()))
                );
        ResponseEntity<EndpointHitDto> response = statsClient.postStat(app, uri, ip, timestamp);
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @AfterEach
    void afterEach() {
        mockServerClient.stop();
    }
}