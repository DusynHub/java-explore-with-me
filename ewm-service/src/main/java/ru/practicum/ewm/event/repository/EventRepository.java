package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.dto.EventFullDto;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<EventFullDto> findAllBy(PageRequest pageRequest);

}
