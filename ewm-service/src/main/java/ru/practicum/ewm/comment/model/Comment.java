package ru.practicum.ewm.comment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.util.DateTimeFormatProvider;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment", schema = "public")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commentator_id")
    private EwmUser commentator;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event commentedEvent;

    @Column(name = "text")
    private String text;

    @Column(name = "edited")
    private boolean edited;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatProvider.PATTERN)
    @Column(name = "commented_on")
    private LocalDateTime commentedOn;

}
