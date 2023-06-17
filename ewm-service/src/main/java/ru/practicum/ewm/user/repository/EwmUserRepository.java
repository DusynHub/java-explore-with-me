package ru.practicum.ewm.user.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.user.model.EwmUser;

import java.util.List;

/**
 * EwmUser Repository for Explore-With-Me
 */
public interface EwmUserRepository extends JpaRepository<EwmUser, Long> {

    /**
     * Method to get all users by ids list
     *
     * @param ids user's ids list
     * @return required users
     */
    List<EwmUser> findAllByIdIn(@Param("ids") List<Long> ids);

    /**
     * Method to get all users by pageRequest
     *
     * @param pageRequest page from and page size in pageRequestForm
     * @return required users
     */
    List<EwmUser> findAllBy(PageRequest pageRequest);

}
