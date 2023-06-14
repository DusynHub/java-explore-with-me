package ru.practicum.ewm.participation_request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.enums.Status;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long>, QuerydslPredicateExecutor<ParticipationRequest> {

    @Query("select count(p) from ParticipationRequest p where p.event.id = :id and p.status = :status")
    long countByEventIdAndStatusEquals(@Param("id") long id, @Param("status") Status status);

    @Modifying(clearAutomatically = true)
    @Query("update ParticipationRequest p set p.status = :status  where p.id = :participationRequest")
    int cancelParticipationRequest(
            @Param("status") Status status,
            @Param("participationRequest") long participationRequest);
}
