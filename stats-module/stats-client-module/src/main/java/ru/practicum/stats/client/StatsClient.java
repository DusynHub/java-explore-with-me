package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.common.stats.dto.EndpointHitDto;

@Service
public class StatsClient extends BaseClient {
    //private static final String API_PREFIX = "/bookings";

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl,
                       RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

//    public ResponseEntity<Object> getBookings(long userId,
//                                              BookingState state,
//                                              Integer from,
//                                              Integer size) {
//        Map<String, Object> parameters = Map.of(
//                "state", state.name(),
//                "from", from,
//                "size", size
//        );
//        return get("?state={state}&from={from}&size={size}", userId, parameters);
//    }

//    public ResponseEntity<Object> getBookingsFromOwner(long userId,
//                                                       BookingState state,
//                                                       Integer from,
//                                                       Integer size) {
//        Map<String, Object> parameters = Map.of(
//                "state", state.name(),
//                "from", from,
//                "size", size
//        );
//        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
//    }
//
//
    public ResponseEntity<Object> postStat( EndpointHitDto endpointHitDto) {
        return post("/hit", endpointHitDto);
    }
//
//    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
//        return get("/" + bookingId, userId);
//    }
//
//    public ResponseEntity<Object> patchBooking(Long userId, Long bookingId, Boolean approved) {
//        Map<String, Object> parameters = Map.of(
//                "bookingId", bookingId,
//                "approved", approved
//        );
//
//        return patch("/{bookingId}?approved={approved}", userId, parameters);
//    }
//

}