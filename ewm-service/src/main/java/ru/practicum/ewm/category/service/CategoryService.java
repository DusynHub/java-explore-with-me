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

    /**
     * Method to get all categories
     *
     * @param fromLine first category
     * @param size page size
     * @return required category list
     */
    List<CategoryDto> getAllCategories(Integer fromLine, Integer size);

    /**
     * Method to get category by id
     *
     * @param categoryId category id
     * @return required category
     */
    CategoryDto getCategory(long categoryId);
}