package ru.practicum.ewm.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.location.model.dto.LocationDto;
import ru.practicum.ewm.util.annotation.EventDateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateEventRequest {

    @Size(min = 20, message = "Annotation length is smaller than 20")
    @Size(max = 2000, message = "Annotation length is bigger than 2000")
    private String annotation;

    private Long category;

    @Size(min = 20, message = "Description length is smaller than 20")
    @Size(max = 7000, message = "Description length is bigger than 2000")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @EventDateValidation
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @Positive(message = "Participant limit must not be positive")
    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, message = "Title length is smaller than 20")
    @Size(max = 120, message = "Title length is bigger than 2000")
    private String title;
}
