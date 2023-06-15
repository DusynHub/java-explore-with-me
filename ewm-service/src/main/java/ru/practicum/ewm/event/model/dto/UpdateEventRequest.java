package ru.practicum.ewm.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.location.model.dto.LocationDto;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateEventRequest {

    private String annotation;

    private long category;

    private String description;

    private String eventDate;

    private LocationDto location;

    private boolean paid;

    private int participantLimit;

    private boolean requestModeration;

    private String stateAction;

    private String title;
}