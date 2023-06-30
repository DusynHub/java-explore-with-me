package ru.practicum.ewm.comment.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.util.DateTimeFormatProvider;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutputCommentDto {

    private Long id;

    private String commentatorName;

    private String text;

    private Boolean edited;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormatProvider.PATTERN)
    private LocalDateTime commentedOn;
}
