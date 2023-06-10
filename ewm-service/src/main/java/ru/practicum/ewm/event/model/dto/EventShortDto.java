package ru.practicum.ewm.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {

    private long id;

    private String annotation;

    private Category category;

    private int confirmedRequests;

    private String eventDate;

    private EwmShortUserDto initiator;

    private boolean paid;

    private String title;

    private int views;
}
