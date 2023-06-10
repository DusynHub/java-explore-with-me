package ru.practicum.ewm.user.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.user.model.EwmUser;

/**
 * Mapper for EwmUser
 */
@Mapper
public interface EwmUserMapper {

    EwmUserMapper INSTANCE = Mappers.getMapper(EwmUserMapper.class);

    EwmUserDto ewmUserToEwmUserDto (EwmUser ewmUser);

    EwmUser ewmUserDtoToEwnUserDto(EwmUserDto ewmUserDto);

    EwmShortUserDto ewmUserToEwmShortUserDto (EwmUser ewmUser);
}
