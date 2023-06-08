package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.model.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    /**
     * Method to save new category
     *
     * @param newCategoryDto new category to be saved
     * @return saved category
     */
    CategoryDto saveCategory(NewCategoryDto newCategoryDto);

    /**
     * Method to update category name by id
     *
     * @param id category id to be updated
     * @param newCategoryDto that contains new category name
     * @return updated category
     */
    CategoryDto patchCategory(long id, NewCategoryDto newCategoryDto);


    /**
     * Method to delete category by id
     *
     * @param id category id to be deleted
     *
     */
    void deleteCategory(long id);

    List<CategoryDto> getAllCategories(Integer fromLine, Integer size);

    CategoryDto getCategory(long categoryId);
}
