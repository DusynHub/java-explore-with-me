package ru.practicum.ewm.event.repository.jpa;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import ru.practicum.ewm.category.model.QCategory;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.model.dto.EventFullDto;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.model.QEwmUser;

import javax.persistence.EntityManager;
import java.util.List;

public class EventRepositoryImpl extends SimpleJpaRepository<Event, Long> implements EventRepository {

    private final Querydsl querydsl;
    private final QEvent qEvent = QEvent.event;
    private final QCategory qCategory = QCategory.category;
    private final QEwmUser qEwmUser= QEwmUser.ewmUser;

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    private QBean<EventFullDto> eventFullDto= Projections.bean(
            EventFullDto.class,
            qEvent.id,
            qEvent.annotation
    );


    public EventRepositoryImpl(EntityManager em) {
        super(Event.class, em);
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
        this.querydsl = new Querydsl(em, (new PathBuilderFactory()).create(Event.class));
    }


    public void clear() {
        em.clear();
    }


    public void detach(Event author) {
        em.detach(author);
    }



    @Override
    public List<EventFullDto> findAllBy(PageRequest pageRequest) {

        JPAQuery<EventFullDto> selectQuery = queryFactory.
                select(eventFullDto)
                .from(qEvent)
                .innerJoin(qEvent.category, qCategory)
                .innerJoin(qEvent.initiator, qEwmUser);

        List<EventFullDto> result = querydsl.applyPagination(pageRequest, selectQuery).fetch();

        System.out.println(result);


        return result;
    }
}
