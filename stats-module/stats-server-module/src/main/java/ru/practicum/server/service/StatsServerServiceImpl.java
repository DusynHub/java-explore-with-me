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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StatsServerServiceImpl implements StatsServerService {

    @PersistenceContext
    private EntityManager entityManager;

    private static final QEndpointHit qEndpointHit = QEndpointHit.endpointHit;


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

        String insertQuery = "INSERT INTO endpoint_hit ( app, uri, ip, timestamp)"
                + " VALUES ( :app, :uri, :ip, :timestamp)";

        entityManager.createNativeQuery(insertQuery)
                .setParameter("app", endpointHitDto.getApp())
                .setParameter("uri", endpointHitDto.getUri())
                .setParameter("ip", endpointHitDto.getIp())
                .setParameter("timestamp", endpointHitDto.getTimestamp())
                .executeUpdate();

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<EndpointHitDto> selectQuery = jpaQueryFactory.from(qEndpointHit)
                .where(qEndpointHit.uri.eq(endpointHitDto.getUri())
                        .and(qEndpointHit.app.eq(endpointHitDto.getApp()))
                        .and(qEndpointHit.ip.eq(endpointHitDto.getIp()))
                        .and(qEndpointHit.timestamp.eq(endpointHitDto.getTimestamp()))
                )
                .select(
                        Projections.bean(
                                EndpointHitDto.class,
                                qEndpointHit.id,
                                qEndpointHit.app,
                                qEndpointHit.uri,
                                qEndpointHit.ip,
                                qEndpointHit.timestamp
                        )
                );
        return selectQuery.fetchFirst();
    }

    @Override
    public List<ViewStatsDto> getEndpointHitsCount(LocalDateTime start,
                                                   LocalDateTime end,
                                                   List<String> endpoints,
                                                   boolean unique) {
        log.info("[StatsService] получение статистики по частоте использвоания эндпоинтов");

        if(start != null && end != null && start.isAfter(end)){
            throw new ValidationException(
                    "Start date must be before End"
            );
        }


        BooleanExpression expression = QEndpointHit.endpointHit.timestamp.after(
                Objects.requireNonNullElseGet(start, LocalDateTime::now));

        if (end != null) {
            expression = expression.and(QEndpointHit.endpointHit.timestamp.before(end));
        }

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
                .where(expression)
                .groupBy(qEndpointHit.app, qEndpointHit.uri)
                .select(viewStats);

        return query.fetch()
                .stream()
                .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                .collect(Collectors.toList());
    }
}
