package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.user.model.dto.EwmShortUserDto;
import ru.practicum.ewm.user.model.dto.EwmUserDto;

import java.util.List;

/**
 * EwmUserService for Explore-with-me
 */
public interface EwmUserService {


    /**
     * Method to get  user entity by id
     *
     * @param ewmUserId  user id
     * @return required user id
     */
    EwmUser getEwmUserEntityByIdMandatory(long ewmUserId);

    /**
     * Method to get  user proxy by id
     *
     * @param ewmUserId  user id
     * @return required user proxy
     */
    EwmUser getEwmUserProxyById(long ewmUserId);


    /**
     * Method to check user existence by id
     *
     * @param userId potentially existed user
     * @return information about user existence
     */
    boolean checkUserExistence(long userId);


    /**
     * Method to get all users
     *
     * @param ewmUserIds user's ids list
     * @param from       first page number
     * @param size       page size
     * @return required users
     */
    List<EwmUserDto> getAllUsersOrUsersByIds(List<Long> ewmUserIds, Integer from, Integer size);

    /**
     * Method to save user
     *
     * @param ewmUserDto user to save
     * @return saved user
     */
    EwmUserDto saveUser(EwmUserDto ewmUserDto);

    /**
     * Method to delete user
     *
     * @param userId id of the user to be deleted
     */
    void deleteUser(long userId);

    /**
     * Method to get user list by ids
     *
     * @param userIds user ids
     * @return  user list
     */
    List<EwmShortUserDto> findAllBy(List<Long> userIds);


}
