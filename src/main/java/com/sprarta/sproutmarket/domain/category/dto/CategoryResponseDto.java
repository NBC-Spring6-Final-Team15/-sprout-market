package com.sprarta.sproutmarket.domain.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponseDto {
    private Long categoryId;
    private String categoryName;
}
