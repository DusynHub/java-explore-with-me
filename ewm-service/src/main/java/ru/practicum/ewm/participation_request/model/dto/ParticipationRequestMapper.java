package ru.practicum.ewm.participation_request.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.dto.EventMapper;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;
import ru.practicum.ewm.user.model.EwmUser;

@Mapper
public interface ParticipationRequestMapper {

    ParticipationRequestMapper INSTANCE = Mappers.getMapper(ParticipationRequestMapper.class);


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


    @Mapping(target = "id",
            ignore = true)
    @Mapping(target = "event",
            source = "participationRequest.event.id")
    @Mapping(target = "requester",
            source = "participationRequest.requester.id")
    ParticipationRequestDto  participationRequestToParticipationRequestDto(
            ParticipationRequest  participationRequest
    );

}
