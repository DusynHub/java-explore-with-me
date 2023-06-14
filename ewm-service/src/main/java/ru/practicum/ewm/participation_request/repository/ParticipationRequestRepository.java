package ru.practicum.ewm.participation_request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.enums.Status;
import ru.practicum.ewm.participation_request.model.ParticipationRequest;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("select count(p) from ParticipationRequest p where p.event.id = :id and p.status = :status")
    long countByEventIdAndStatusEquals(@Param("id") long id, @Param("status") Status status);
}
