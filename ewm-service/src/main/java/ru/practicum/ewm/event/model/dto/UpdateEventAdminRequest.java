package ru.practicum.ewm.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.enums.StateAction;
import ru.practicum.ewm.location.model.dto.LocationDto;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {

    private String annotation;

    private Category category;

    private String description;

    private String eventDate;

    private LocationDto location;

    private boolean paid;

    private int participantLimit;

    private boolean requestModeration;

    private String stateAction;

    private String title;
}
