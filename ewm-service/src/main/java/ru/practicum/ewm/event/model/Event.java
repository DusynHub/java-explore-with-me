package ru.practicum.ewm.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.user.model.EwmUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;


@Entity
@Table(name = "event", schema = "public")
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Event {

//  obligatory fields

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "annotation")
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    @ToString.Exclude
    private EwmUser initiator;

    @Column(name = "lat")
    private double lat;

    @Column(name = "lon")
    private double lon;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "title")
    private String title;

//  optional fields

    @Column(name = "description")
    private String description;

    @Column(name = "created_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "current_participant_amount")
    private int currentParticipantsAmount;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "views")
    private int views;

    public long getId() {
        return this.id;
    }

    public String getAnnotation() {
        return this.annotation;
    }

    public Category getCategory() {
        return this.category;
    }

    public LocalDateTime getEventDate() {
        return this.eventDate;
    }

    public EwmUser getInitiator() {
        return this.initiator;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    public boolean isPaid() {
        return this.paid;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public LocalDateTime getCreatedOn() {
        return this.createdOn;
    }

    public LocalDateTime getPublishedOn() {
        return this.publishedOn;
    }

    public int getParticipantLimit() {
        return this.participantLimit;
    }

    public int getCurrentParticipantsAmount() {
        return this.currentParticipantsAmount;
    }

    public boolean isRequestModeration() {
        return this.requestModeration;
    }

    public State getState() {
        return this.state;
    }

    public int getViews() {
        return this.views;
    }
}
