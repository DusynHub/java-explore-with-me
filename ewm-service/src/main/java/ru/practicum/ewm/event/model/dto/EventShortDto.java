package ru.practicum.ewm.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {

    private Long id;

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    private String eventDate;

    private EwmShortUserDto initiator;

    private Boolean paid;

    private String title;

    private Integer views;
}
