package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
@Validated
public class PublicCategoriesController {

    private final CategoryService categoryService;

    @GetMapping
    List<CategoryDto> getAllCategories(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("[Public Controller] received a request GET /categories");
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{categoryId}")
    CategoryDto getCategory(
            @PathVariable @Positive String categoryId
    ) {
        log.info("[Public Controller] received a request GET /categories");
        long categoryIdLong = Long.parseLong(categoryId);
        return categoryService.getCategory(categoryIdLong);
    }
}
