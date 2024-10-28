package com.sprarta.sproutmarket.domain.interestedCategory.repository;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.interestedCategory.entity.InterestedCategory;
import com.sprarta.sproutmarket.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterestedCategoryRepository extends JpaRepository<InterestedCategory, Long> {
    boolean existsByUserAndCategory(User user, Category category);
    List<User> findUsersByCategoryId(Long categoryId);
    Optional<InterestedCategory> findByUserAndCategory(User user, Category category);
}
