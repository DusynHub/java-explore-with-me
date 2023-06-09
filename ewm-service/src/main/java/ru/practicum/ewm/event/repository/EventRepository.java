package ru.practicum.ewm.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;


@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Modifying(clearAutomatically = true)
    @Query("update Event e set e.confirmedRequests = e.confirmedRequests + :number where e.id = :eventId")
    int increaseByNumberConfirmedByEventId(@Param("number") int number, @Param("eventId") long eventId);

    @Modifying(clearAutomatically = true)
    @Query("update Event e set e.confirmedRequests = e.confirmedRequests - :number where e.id = :eventId")
    int decreaseByNumberConfirmedByEventId(@Param("number") int number, @Param("eventId") long eventId);

}
