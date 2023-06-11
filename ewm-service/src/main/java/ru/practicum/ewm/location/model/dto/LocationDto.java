package ru.practicum.ewm.location.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    private double lat;

    private  double lon;
}
