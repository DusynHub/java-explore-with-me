package ru.practicum.ewm.comment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    @NotNull(message = "Commentator id can't be null")
    @PositiveOrZero(message = "commentator id can't be negative")
    private Long commentator;

    @NotNull(message = "Commented event id can't be null")
    @PositiveOrZero(message = "Commented event id can't be negative")
    private Long commentedEvent;

    @NotBlank(message = "Text can't be blank")
    @Size(max = 20000, message = "Text length is bigger than 20 000")
    private String text;

    private Boolean edited;

    @FutureOrPresent(message = "Comment date can't be in the past")
    private LocalDateTime commentedOn;
}
