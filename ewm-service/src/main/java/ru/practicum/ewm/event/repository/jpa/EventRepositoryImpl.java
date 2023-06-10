package ru.practicum.ewm.event.repository.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;

import javax.persistence.EntityManager;

public class EventRepositoryImpl extends SimpleJpaRepository<Event, Long> implements EventRepository {

    private final QEvent qEvent = QEvent.event;

    private final EntityManager em;

    protected final JPAQueryFactory queryFactory;


    public EventRepositoryImpl( EntityManager em) {
        super(Event.class, em);
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);;
    }


    public void clear() {
        em.clear();
    }


    public void detach(Event author) {
        em.detach(author);
    }
}
