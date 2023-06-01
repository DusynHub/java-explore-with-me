package ru.practicum.stats.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class StatsClientTest {

    private final static String statsServerUrl = "http://localhost:5050";

    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    RestTemplate rest = restTemplateBuilder.uriTemplateHandler(new DefaultUriBuilderFactory(statsServerUrl))
            .requestFactory(HttpComponentsClientHttpRequestFactory::new)
            .build();
    private final StatsClient statsClient = new StatsClient(statsServerUrl, restTemplateBuilder);

    MockRestServiceServer mockServer =
            MockRestServiceServer.bindTo(rest).build();

    @Test
    void getStat() {
        String startDate = "2022-09-06 12:00:54";
        String endDate = "2022-02-07 11:11:55";
        List<String> uriL = List.of("/event", "/event/1");
        boolean uniques = false;

        mockServer.expect(requestTo("/stats"))
                .andRespond(withSuccess());

        statsClient.getStat(startDate, endDate, uriL, uniques);
    }
}