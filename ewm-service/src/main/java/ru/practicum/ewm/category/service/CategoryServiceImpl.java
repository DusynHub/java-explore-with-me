package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.model.dto.CategoryMapper;
import ru.practicum.ewm.category.model.dto.NewCategoryDto;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.ResourceNotFoundException;
import ru.practicum.ewm.util.OffsetPageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        log.info("[Service] received a request to save category");
        Category categoryToSave = CategoryMapper.INSTANCE.newCategoryDtoToCategory(newCategoryDto);
        return  CategoryMapper.INSTANCE.categoryToCategoryDto(
                categoryRepository.save(categoryToSave)
        );
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(long id, NewCategoryDto newCategoryDto) {
        log.info("[Service] received a request to patch category");
        categoryRepository.updateCategoryById(id, newCategoryDto.getName());
        return CategoryMapper.INSTANCE.categoryToCategoryDto(
                categoryRepository.findById(id).orElseThrow(
                        () -> new ResourceNotFoundException(
                                String.format("Category with id = '%d' not found", id)
                        )
                )
        );
    }

    @Override
    @Transactional
    public void deleteCategory(long categoryId) {
        log.info("[Service] received a request to delete category");
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException(
                    String.format("User with id = '%d' not found", categoryId)
            );
        }

//        TODO нужно дописать проверку наличия событий с такой категорией, если есть то выбросить ошибку конфликт с кодом 409

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer fromLine, Integer size){
        log.info("[Service] received a request to delete category");
        OffsetPageRequest pageRequest = OffsetPageRequest.of(fromLine, size);
        return categoryRepository.findAllBy(pageRequest)
                .stream()
                .map(CategoryMapper.INSTANCE::categoryToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(long categoryId){
        log.info("[Service] received a request to get category with id = '{}'", categoryId);
        return CategoryMapper.INSTANCE.categoryToCategoryDto(
            categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException(
                    String.format("Category with id = '%d' not found", categoryId)
                )
            )
        );
    }
}
