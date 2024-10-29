package com.sprarta.sproutmarket.domain.interestedCategory.entity;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@RequiredArgsConstructor
public class InterestedCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public InterestedCategory(User user, Category category) {
        this.user = user;
        this.category = category;
    }

    // 연관관계 편의 메서드: User와 Category에 이 엔티티를 추가하는 메서드
    public void setUser(User user) {
        this.user = user;
        // 관심 카테고리가 추가될 때 사용자의 관심 카테고리 목록에도 추가
        user.getInterestedCategories().add(this);
    }

    public void setCategory(Category category) {
        this.category = category;
        // 관심 카테고리가 추가될 때 카테고리의 관심 사용자 목록에도 추가
        category.getInterestedCategories().add(this);
    }
}
