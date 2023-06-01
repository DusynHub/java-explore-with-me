package ru.practicum.server.service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.common.stats.dto.ViewStatsDto;
import ru.practicum.common.stats.model.QEndpointHit;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.repository.StatsServiceRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StatsServerServiceImpl implements StatsServerService {

    @PersistenceContext
    private EntityManager entityManager;

    private final StatsServiceRepository statsServiceRepository;

    private final QEndpointHit qEndpointHit = QEndpointHit.endpointHit;


    @Override
    @Transactional
    public EndpointHitDto addEndPointHit(EndpointHitDto endpointHitDto) {
        log.info("[StatsService] получен запрос сохранение информация об обращении " +
                        "в приложении - '{}' " +
                        "по uri-'{}' " +
                        "от ip - '{}']",
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp()
        );
        return EndpointHitMapper.INSTANCE.endpointHitToEndpointHitDto(
                statsServiceRepository.save(
                        EndpointHitMapper.INSTANCE.endpointHitDtoToEndpointHit(endpointHitDto)
                )
        );
    }

    @Override
    public List<ViewStatsDto> getEndpointHitsCount(LocalDateTime start,
                                                   LocalDateTime end,
                                                   List<String> endpoints,
                                                   boolean unique) {
        log.info("[StatsService] получение статистики по частоте использвоания эндпоинтов");



        BooleanExpression byUri;
        QBean<ViewStatsDto> viewStats;
        if (endpoints.isEmpty()) {
            byUri = qEndpointHit.uri.notIn(endpoints);
        } else {
            byUri = qEndpointHit.uri.in(endpoints);
        }
        if (unique) {
            viewStats = Projections.bean(
                    ViewStatsDto.class,
                    qEndpointHit.app,
                    qEndpointHit.uri,
                    qEndpointHit.ip.countDistinct().as("hits"));
        } else {
            viewStats = Projections.bean(
                    ViewStatsDto.class,
                    qEndpointHit.app,
                    qEndpointHit.uri,
                    qEndpointHit.ip.count().as("hits"));
        }
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        JPAQuery<ViewStatsDto> query = jpaQueryFactory.from(qEndpointHit)
                .where(byUri)
                .groupBy(qEndpointHit.app, qEndpointHit.uri)
                .select(viewStats);

        return query.fetch()
                .stream()
                .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                .collect(Collectors.toList());
    }
}
