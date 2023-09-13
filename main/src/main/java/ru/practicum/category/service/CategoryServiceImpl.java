package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.entity.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsCategoryByName(newCategoryDto.getName())) {
            throw new ConflictException("Такая категория уже есть");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        getCategory(catId);
        if (categoryRepository.existsCategoryByNameAndIdNot(categoryDto.getName(), catId)) {
            throw new ConflictException("Такая категория уже есть");
        }
        categoryDto.setId(catId);
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    public void deleteCategories(Long catId) {
        Category category = getCategory(catId);
        if (eventRepository.existsEventsByCategoryId(catId)) {
            throw new ConflictException("Попытка удалить категория привязанную к событию");
        }
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return CategoryMapper
                .toCategoryDto(categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Category> categoriesList = categoryRepository.findAll(page).getContent();
        return categoriesList.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    private Category getCategory(Long catId) {
        return categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
    }
}
