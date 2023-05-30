package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.common.stats.model.EndpointHit;

public interface StatsServiceRepository extends JpaRepository<EndpointHit, Long> {


}
