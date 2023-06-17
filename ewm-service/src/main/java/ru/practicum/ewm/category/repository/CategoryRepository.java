package ru.practicum.ewm.category.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Method to get all categories by pageRequest
     *
     * @param pageRequest page from and page size in pageRequestForm
     * @return required categories
     */
    List<Category> findAllBy(PageRequest pageRequest);
}
