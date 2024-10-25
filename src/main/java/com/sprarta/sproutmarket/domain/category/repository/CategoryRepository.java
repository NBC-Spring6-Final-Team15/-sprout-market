package com.sprarta.sproutmarket.domain.category.repository;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(@NotBlank String categoryName);
}
