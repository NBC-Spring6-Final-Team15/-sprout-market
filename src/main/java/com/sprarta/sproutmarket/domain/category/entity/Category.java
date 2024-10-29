package com.sprarta.sproutmarket.domain.category.entity;

import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.interestedCategory.entity.InterestedCategory;
import com.sprarta.sproutmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "category")
    private List<InterestedCategory> interestedCategories = new ArrayList<>();

    public void update(String name) {
        this.name = name;
    }

    public void deactivate() {
        this.status = Status.DELETED;
    }

    public void activate() {
        this.status = Status.ACTIVE;
    }

    public Category(String name) {
        this.name = name;
    }

    // 관심 카테고리 추가 메서드
    public void addInterestedCategory(InterestedCategory interestedCategory) {
        interestedCategories.add(interestedCategory);
        interestedCategory.setCategory(this);
    }

    // 관심 카테고리 제거 메서드
    public void removeInterestedCategory(User user) {
        interestedCategories.removeIf(interestedCategory -> interestedCategory.getUser().equals(user));
    }
}
