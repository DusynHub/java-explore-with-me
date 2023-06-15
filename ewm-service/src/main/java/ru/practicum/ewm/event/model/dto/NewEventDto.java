package ru.practicum.ewm.event.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class NewEventDto {

    @NotBlank(message = "Annotation can't be blank")
    @Size(min = 20, message = "Annotation length is smaller than 20")
    @Size(max = 2000, message = "Annotation length is bigger than 2000")
    private String annotation;

    @Positive
    private Long category;

    @NotBlank(message = "Description can't be blank")
    @Size(min = 20, message = "Description length is smaller than 20")
    @Size(max = 7000, message = "Description length is bigger than 2000")
    private String description;

    @NotNull(message = "Event date must not be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @EventDateValidation
    private LocalDateTime eventDate;

    @NotNull(message = "Location must not be null")
    private LocationDto location;

    private Boolean paid;

    @Positive(message = "Participant limit must not be positive")
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank(message = "Title can't be blank")
    @Size(min = 3, message = "Title length is smaller than 20")
    @Size(max = 120, message = "Title length is bigger than 2000")
    private String title;
}
