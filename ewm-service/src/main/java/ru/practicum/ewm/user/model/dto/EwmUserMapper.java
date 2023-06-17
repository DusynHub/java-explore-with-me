package ru.practicum.ewm.user.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.user.model.EwmUser;

/**
 * Mapper for EwmUser
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Service
public interface EwmUserMapper {

    EwmUserDto ewmUserToEwmUserDto(EwmUser ewmUser);

    EwmUser ewmUserDtoToEwnUserDto(EwmUserDto ewmUserDto);

    EwmShortUserDto ewmUserToEwmShortUserDto(EwmUser ewmUser);
}
