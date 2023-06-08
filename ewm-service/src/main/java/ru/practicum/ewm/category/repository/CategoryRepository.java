package ru.practicum.ewm.category.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {


    /**
     * Method for admin to change existing category name
     *
     * @param id category id which name has to be changed
     * @param newName new name of the category
     */
    @Modifying
    @Query("UPDATE Category c SET c.name = :newName WHERE c.id = :id")
    void updateCategoryById(@Param("id") long id, @Param("newName") String newName);


    /**
     * Method to get all categories by pageRequest
     *
     * @param pageRequest page from and page size in pageRequestForm
     * @return required categories
     */
    List<Category> findAllBy(PageRequest pageRequest);

}
