package ru.practicum.ewm.comment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {

    private Long id;

    private Long commentator;

    private Long commentedEvent;

    @NotBlank(message = "Text can't be blank")
    @Size(max = 20000, message = "Text length is bigger than 20 000")
    private String text;
}
