package ru.practicum.ewm.category.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.model.dto.CategoryDto;
import ru.practicum.ewm.category.model.dto.CategoryMapper;
import ru.practicum.ewm.category.model.dto.NewCategoryDto;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ResourceConflictException;
import ru.practicum.ewm.exception.ResourceNotFoundException;
import ru.practicum.ewm.util.OffsetPageRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        log.info("[Category Service] received an admin request to save category");
        Category categoryToSave = CategoryMapper.INSTANCE.newCategoryDtoToCategory(newCategoryDto);
        return CategoryMapper.INSTANCE.categoryToCategoryDto(
                categoryRepository.save(categoryToSave)
        );
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(long id, NewCategoryDto newCategoryDto) {
        log.info("[Category Service] received an admin request to patch category");
        Category categoryToBePatched = getCategoryByIdMandatory(id);
        CategoryMapper.INSTANCE.newCategoryDtoToCategory(newCategoryDto, categoryToBePatched);
        return CategoryMapper.INSTANCE.categoryToCategoryDto(
                categoryRepository.save(categoryToBePatched)
        );
    }

    @Override
    @Transactional
    public void deleteCategory(long categoryId) {
        log.info("[Category Service] received an admin request to delete category");
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException(
                    String.format("User with id = '%d' not found", categoryId)
            );
        }

        BooleanExpression findByCategory = QEvent.event.category.id.eq(categoryId);

        List<Event> eventsLinkedToCategory = StreamSupport.stream(
                eventRepository.findAll(findByCategory).spliterator(), false
        ).collect(Collectors.toList());

        if (!eventsLinkedToCategory.isEmpty()) {
            throw new ResourceConflictException("The category is not empty");
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryDto> getAllCategories(int fromLine, int size) {
        log.info("[Category Service] received a public request to get all categories");
        OffsetPageRequest pageRequest = OffsetPageRequest.of(fromLine, size);
        return categoryRepository.findAllBy(pageRequest)
                .stream()
                .map(CategoryMapper.INSTANCE::categoryToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(long categoryId) {
        log.info("[Category Service] received a public request to get category with id = '{}'", categoryId);
        return CategoryMapper.INSTANCE.categoryToCategoryDto(
                getCategoryByIdMandatory(categoryId)
        );
    }

    @Override
    public Category getCategoryEntity(long categoryId) {
        log.info("[Category Service] received a request to get category entity with id = '{}'", categoryId);
        return getCategoryByIdMandatory(categoryId);
    }

    @Override
    public Category getCategoryProxyById(long categoryId) {
        log.info("[Category Service] received a request to get category proxy with id = '{}'", categoryId);
        return categoryRepository.getReferenceById(categoryId);
    }

    @Override
    public List<CategoryDto> findAllById(List<Long> categoryIds) {
        return categoryRepository.findAllById(categoryIds)
                .stream()
                .map(CategoryMapper.INSTANCE::categoryToCategoryDto)
                .collect(Collectors.toList());
    }

    private Category getCategoryByIdMandatory(long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        String.format("Category with id = '%d' not found", id)
                )
        );
    }
}
