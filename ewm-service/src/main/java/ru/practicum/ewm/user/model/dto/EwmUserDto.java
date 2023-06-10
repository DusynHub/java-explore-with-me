package ru.practicum.ewm.user.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import ru.practicum.ewm.util.annotation.DomainLength;
import ru.practicum.ewm.util.annotation.LocalPartLength;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EwmUserDto {

    private long id;

    @NotBlank(message = "Name can't be blank")
    @Size(min = 2, message = "Name length is smaller than 2")
    @Size(max = 250, message = "Name length is bigger than 250")
    private String name;

    @NotBlank
    @Email
    @Size(min = 6, message = "Email length is smaller than 6")
    @Size(max = 254, message = "Email length is bigger than 254")
    @LocalPartLength(message = "Local part length is bigger than 64")
    @DomainLength(message = "Domain length is bigger than 63")
    private String email;
}
