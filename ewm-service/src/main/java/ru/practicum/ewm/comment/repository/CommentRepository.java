package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    @Query("SELECT  c FROM Comment c " +
            "LEFT JOIN FETCH c.commentator " +
            "LEFT JOIN FETCH c.commentedEvent " +
            "WHERE c.commentedEvent.id = :eventId")
    List<Comment> findAllBy(@Param("eventId") long eventId, PageRequest pageRequest);

}
