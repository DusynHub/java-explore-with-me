package ru.practicum.ewm.participation_request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation_request.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userIdString}/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PrivateParticipationRequestController {

    private final ParticipationRequestService participationRequestService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postRequest(
            @PathVariable String userIdString,
            @RequestParam(name = "eventId") String eventIdString) {
        log.info("[Private Participation Request Controller] received a request POST users/{}/requests", userIdString);
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);

        return participationRequestService.postRequest(userId, eventId);
    }

    @PatchMapping("/{requestIdString}/cancel")
    public ParticipationRequestDto cancelRequest(
            @PathVariable String userIdString,
            @PathVariable String requestIdString) {
        log.info("[Private Participation Request Controller] received a request PATCH users/{}/requests/{}/cancel", userIdString, requestIdString);
        long userId = Long.parseLong(userIdString);
        long requestId = Long.parseLong(requestIdString);

        return participationRequestService.cancelParticipationRequest(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getUserParticipationRequests(@PathVariable String userIdString) {
        log.info("[Private Participation Request Controller] received a request GET users/{}/requests", userIdString);
        long userId = Long.parseLong(userIdString);

        return participationRequestService.getUserParticipationRequests(userId);
    }

}
