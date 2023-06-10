package ru.practicum.ewm.participation_request.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {

    private long id;

    @Positive
    private long event;

    @Positive
    private long requester;

    @NotBlank
    private String status;

    @NotBlank
    private String created;
}
