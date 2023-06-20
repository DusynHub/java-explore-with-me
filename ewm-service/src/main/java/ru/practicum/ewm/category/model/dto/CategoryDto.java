package ru.practicum.ewm.category.model.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private long id;

    @NotBlank(message = "Name can't be blank")
    @Size(min = 1, message = "Name length is smaller than 1")
    @Size(max = 50, message = "Name length is bigger than 50")
    private String name;
}

