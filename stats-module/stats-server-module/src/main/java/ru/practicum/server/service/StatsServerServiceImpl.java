package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.repository.StatsServiceRepository;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StatsServerServiceImpl implements StatsServerService{

    private final StatsServiceRepository statsServiceRepository;

    @Override
    public EndpointHitDto addEndPointHit(EndpointHitDto endpointHitDto) {

        return EndpointHitMapper.INSTANCE.endpointHitToEndpointHitDto(
                statsServiceRepository.save(
                        EndpointHitMapper.INSTANCE.endpointHitDtoToEndpointHit(endpointHitDto)
                )
        );
    }
}
