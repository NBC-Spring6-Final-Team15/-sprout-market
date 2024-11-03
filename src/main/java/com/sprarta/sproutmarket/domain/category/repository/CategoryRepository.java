package com.sprarta.sproutmarket.domain.category.repository;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(@NotBlank String categoryName);

    List<Category> findAllByStatus(Status status);

    @Query("SELECT c FROM Category c where c.id = :categoryId AND c.status = 'ACTIVE'")
    Optional<Category> findByIdAndStatusIsActive(@Param("categoryId") Long categoryId);

    default Category findByIdAndStatusIsActiveOrElseThrow(Long categoryId) {
        return findByIdAndStatusIsActive(categoryId).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_FOUND_CATEGORY)
        );
    }

    default Category findByIdOrElseThrow(Long categoryId) {
        return findById(categoryId).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_FOUND_CATEGORY)
        );
    }
}
