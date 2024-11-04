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

    // 특정 사용자와 카테고리의 관심 정보가 존재하는지 확인
    boolean existsByUserAndCategory(User user, Category category);

    // 특정 사용자와 카테고리로 관심 카테고리 찾기
    Optional<InterestedCategory> findByUserAndCategory(User user, Category category);

    // 카테고리에 관심 있는 사용자 목록 조회
    @Query("SELECT ic.user FROM InterestedCategory ic WHERE ic.category.id = :categoryId")
    List<User> findUsersByCategoryId(@Param("categoryId") Long categoryId);
}