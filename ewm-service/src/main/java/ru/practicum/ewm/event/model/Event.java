package ru.practicum.ewm.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.user.model.EwmUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;


@Entity
@Table(name = "event", schema = "public")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

//  obligatory fields

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "annotation")
    private String annotation;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator",  nullable = false)
    private EwmUser initiator;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location",  nullable = false)
    private Location location;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "title")
    private String title;

//    optional fields

    @Column(name = "description")
    private String description;

    @Column(name = "created_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss")
    private LocalDateTime publishedOn;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Column(name = "views")
    private int views;
}
