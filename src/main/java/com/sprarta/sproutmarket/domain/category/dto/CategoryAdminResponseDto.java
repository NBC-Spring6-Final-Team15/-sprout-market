package com.sprarta.sproutmarket.domain.category.dto;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryAdminResponseDto {
    private Long categoryId;
    private String categoryName;
    private Status status;

    public CategoryAdminResponseDto(Category category) {
        this.categoryId = category.getId();
        this.categoryName = category.getName();
        this.status = category.getStatus();
    }
}
