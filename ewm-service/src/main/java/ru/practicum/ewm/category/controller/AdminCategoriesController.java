package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.model.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

/**
 * Controller for admin endpoints for category
 */
@RestController
@Slf4j
@RequestMapping(path = "/admin/categories")
@Validated
@RequiredArgsConstructor
public class AdminCategoriesController {

    private final CategoryService categoryService;

    /**
     * Method for admin to save new category
     *
     * @param newCategoryDto new category to save
     * @return saved category
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto postCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("[Admin Controller] received a request POST /admin/categories with name {}",
                newCategoryDto.getName());
        return categoryService.saveCategory(newCategoryDto);
    }

    /**
     * Method for admin to delete category by id
     *
     * @param categoryId category id to be deleted
     */
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable @Positive String categoryId) {
        log.info("[Admin Controller] received a request DELETE /admin/categories/{}", categoryId);
        long categoryIdLong = Long.parseLong(categoryId);
        categoryService.deleteCategory(categoryIdLong);
    }

    /**
     * Method for admin to change category name
     *
     * @param categoryId     category id to be changed
     * @param newCategoryDto new category name
     * @return category with changed name
     */
    @PatchMapping("/{categoryId}")
    CategoryDto patchCategory(
            @PathVariable String categoryId,
            @RequestBody(required = false) @Valid NewCategoryDto newCategoryDto) {
        log.info("[Admin Controller] received a request PATCH /admin/categories/{}", categoryId);
        long categoryIdLong = Long.parseLong(categoryId);
        return categoryService.patchCategory(categoryIdLong, newCategoryDto);
    }
}
