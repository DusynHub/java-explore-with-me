package ru.practicum.ewm.participation_request.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.dto.EventMapper;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;
import ru.practicum.ewm.user.model.EwmUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(imports = LocalDateTime.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParticipationRequestMapper {

    ParticipationRequestMapper INSTANCE = Mappers.getMapper(ParticipationRequestMapper.class);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Mapping(target = "id",
            ignore = true)
    @Mapping(target = "event",
            source = "eventProxy")
    @Mapping(target = "requester",
            source = "ewmUserProxy")
    ParticipationRequest  participationRequestDtoToParticipationRequest(
            ParticipationRequestDto  participationRequestDto,
            Event eventProxy,
            EwmUser ewmUserProxy
    );


    @Mapping(target = "event",
            source = "participationRequest.event.id")
    @Mapping(target = "requester",
            source = "participationRequest.requester.id")
    @Mapping(target = "created",
            expression = "java(participationRequest.getCreated().format(formatter))")
    ParticipationRequestDto  participationRequestToParticipationRequestDto(
            ParticipationRequest  participationRequest
    );

}
