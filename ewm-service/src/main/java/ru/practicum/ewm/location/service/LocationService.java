package ru.practicum.ewm.location.service;

import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.model.dto.LocationMapper;

public interface LocationService {

    Location saveLocation(Location location);
}
