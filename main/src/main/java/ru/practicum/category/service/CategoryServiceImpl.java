package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.entity.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
//    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
//        if (categoryRepository.existsCategoriesByName(newCategoryDto.getName())) {
//            throw new ConflictException("Такая категория уже есть");
//        }
        Category category = CategoryMapper.toCategory(newCategoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = getCategory(categoryDto.getId());
//        if (categoryRepository.existsCategoriesByNameAndIdNot(categoryDto.getName(), categoryDto.getId())) {
//            throw new ConflictException("Такая категория уже есть");
//        }
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        getCategory(catId);
        categoryRepository.deleteById(catId);
//        if (category == null) {
//            throw new NotFoundException("Категория не найдена.");
//        }
//
//        if (eventRepository.existsEventsByCategory_Id(catId)) {
//            throw new ConflictException("Такой пользователь уже есть");
//        }
//        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
//        int offset = from > 0 ? from / size : 0;
//        PageRequest page = PageRequest.of(offset, size);
//        List<Category> categoryList = categoryRepository.findAll(page).getContent();
//        return categoryList.stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());

        return categoryRepository.getAllCategories(PageRequest.of(from, size))
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return categoryRepository.findById(catId)
                .map(CategoryMapper::toCategoryDto)
                .orElseThrow(() -> new NotFoundException("Категория по id не найдена."));
    }

    private Category getCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Категория не найдена."));
    }
}
