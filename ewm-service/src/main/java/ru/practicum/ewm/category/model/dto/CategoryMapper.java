package ru.practicum.ewm.category.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.category.model.Category;

/**
 * Mapper for Category
 */
@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDto categoryToCategoryDto (Category category);

    Category newCategoryDtoToCategory (NewCategoryDto newCategoryDto);

}
