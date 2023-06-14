package ru.practicum.ewm.participation_request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.participation_request.model.dto.ParticipationRequestDto;
import ru.practicum.ewm.participation_request.service.ParticipationRequestService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users/{userIdString}/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
public class PrivateParticipationRequestController {

    private final ParticipationRequestService participationRequestService;


    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postRequest(
            @PathVariable String userIdString,
            @RequestParam(name = "eventId") String eventIdString){
        log.info("[Private Participation Request Controller] received a request POST users/{}/requests", userIdString);
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);

        return participationRequestService.postRequest(userId,  eventId);
    }
}
