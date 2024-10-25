package com.sprarta.sproutmarket.domain.category.dto;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponseDto {
    private Long categoryId;
    private String categoryName;

    public CategoryResponseDto(Category category) {
        this.categoryId = category.getId();
        this.categoryName = category.getName();
    }
}
