package ru.practicum.category.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.category.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select c from Category as c order by c.id desc ")
    List<Category> getAllCategories(Pageable pageable);
//
//    boolean existsCategoriesByName(String name);
//
//    boolean existsCategoriesByNameAndIdNot(String name, Long id);
}
