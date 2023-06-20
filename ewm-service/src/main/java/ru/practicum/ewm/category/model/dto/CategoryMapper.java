package ru.practicum.ewm.category.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;

/**
 * Mapper for Category
 */
@Mapper(componentModel = "spring")
@Service
public interface CategoryMapper {

    CategoryDto categoryToCategoryDto(Category category);

    Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);

    void newCategoryDtoToCategory(NewCategoryDto newCategoryDto,
                                  @MappingTarget Category categoryToBePatched);

}
