package ru.practicum.server.service;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.stats.dto.EndpointHitDto;
import ru.practicum.common.stats.dto.ViewStatsDto;
import ru.practicum.common.stats.model.EndpointHit;
import ru.practicum.common.stats.model.QEndpointHit;
import ru.practicum.server.mapper.EndpointHitMapper;
import ru.practicum.server.repository.StatsServiceRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StatsServerServiceImpl implements StatsServerService{

    @PersistenceContext
    private EntityManager entityManager;

    private final StatsServiceRepository statsServiceRepository;

    private final QEndpointHit qEndpointHit = QEndpointHit.endpointHit;


    @Override
    @Transactional
    public EndpointHitDto addEndPointHit(EndpointHitDto endpointHitDto) {

        return EndpointHitMapper.INSTANCE.endpointHitToEndpointHitDto(
                statsServiceRepository.save(
                        EndpointHitMapper.INSTANCE.endpointHitDtoToEndpointHit(endpointHitDto)
                )
        );
    }

    @Override
    public List<ViewStatsDto> getNonUniqueEndpointHitsCount(LocalDateTime start,
                                                            LocalDateTime end,
                                                            List<String> endpoints){

        BooleanExpression byUri;

        if(endpoints.isEmpty()){
            byUri = qEndpointHit.uri.notIn(endpoints);
        } else {
            byUri = qEndpointHit.uri.in(endpoints);
        }

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

                JPAQuery<ViewStatsDto> query = jpaQueryFactory.from(qEndpointHit)
                        .where(byUri)
                        .groupBy(qEndpointHit.app, qEndpointHit.uri)
                        .select(
                                Projections.bean(
                                        ViewStatsDto.class,
                                        qEndpointHit.app,
                                        qEndpointHit.uri,
                                        qEndpointHit.ip.count().as("hits")
                                )
                        );

        return query.fetch().stream().sorted(Comparator.comparing(ViewStatsDto::getHits).reversed()).collect(Collectors.toList());
    }

    @Override
    public List<ViewStatsDto> getUniqueEndpointHitsCount(LocalDateTime start,
                                                         LocalDateTime end,
                                                         List<String> endpoints){
        BooleanExpression byUri;
        if(endpoints.isEmpty()){
            byUri = qEndpointHit.uri.notIn(endpoints);
        } else {
            byUri = qEndpointHit.uri.in(endpoints);
        }
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<EndpointHit> query = jpaQueryFactory.from(qEndpointHit)
                .where(byUri)
                .groupBy(qEndpointHit.app, qEndpointHit.uri, qEndpointHit.ip)
                .select(
                        Projections.bean(
                                EndpointHit.class, qEndpointHit.app, qEndpointHit.uri, qEndpointHit.ip
                        )
                );
        Function<EndpointHit, List<String>> classifier = (endpointHitMap) -> List.of(
                endpointHitMap.getApp(),
                endpointHitMap.getUri()
        );

        Map<List<String>, Long> viewStatsPropertiesWithHits =
                query.fetch().stream()
                        .collect(Collectors.groupingBy(classifier, Collectors.counting()));

        List<ViewStatsDto> result = new ArrayList<>(viewStatsPropertiesWithHits.size());
        for(Map.Entry<List<String>, Long> viewStatsProperties : viewStatsPropertiesWithHits.entrySet()){
            result.add(mapListToViewStatsDto(viewStatsProperties.getKey(), viewStatsProperties.getValue()));
        }
        return result.stream().sorted(Comparator.comparing(ViewStatsDto::getHits).reversed()).collect(Collectors.toList());
    }
    private ViewStatsDto mapListToViewStatsDto(List<String> properties, Long hits){
        return new ViewStatsDto(properties.get(0), properties.get(1), hits);
    }
}
