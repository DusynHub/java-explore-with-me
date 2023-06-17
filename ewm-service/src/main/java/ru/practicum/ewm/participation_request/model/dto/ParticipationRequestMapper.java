package ru.practicum.ewm.participation_request.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring",
        imports = LocalDateTime.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Service
public interface ParticipationRequestMapper {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(target = "event",
            source = "participationRequest.event.id")
    @Mapping(target = "requester",
            source = "participationRequest.requester.id")
    @Mapping(target = "created",
            expression = "java(participationRequest.getCreated().format(formatter))")
    ParticipationRequestDto participationRequestToParticipationRequestDto(
            ParticipationRequest participationRequest
    );
}
