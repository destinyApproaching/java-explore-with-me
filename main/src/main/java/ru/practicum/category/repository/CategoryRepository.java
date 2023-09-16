package ru.practicum.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsCategoryByName(String name);

    boolean existsCategoryByNameAndIdNot(String name, Long id);
}
