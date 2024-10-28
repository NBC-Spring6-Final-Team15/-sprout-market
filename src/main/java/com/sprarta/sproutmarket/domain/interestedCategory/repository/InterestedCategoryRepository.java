package com.sprarta.sproutmarket.domain.interestedCategory.repository;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.interestedCategory.entity.InterestedCategory;
import com.sprarta.sproutmarket.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestedCategoryRepository extends JpaRepository<InterestedCategory, Long> {
    boolean existsByUserAndCategory(User user, Category category);
    @Query("SELECT ic.user FROM InterestedCategory ic WHERE ic.category.id = :categoryId")
    List<User> findUsersByCategoryId(@Param("categoryId") Long categoryId);
    Optional<InterestedCategory> findByUserAndCategory(User user, Category category);
}
