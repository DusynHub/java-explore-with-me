package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {

    @JsonProperty("status")
    private final HttpStatus status;

    @JsonProperty("reason")
    private final String reason;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("timestamp")
    private final String timestamp;
}
