package com.sprarta.sproutmarket.domain.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  // 기본생성자
@AllArgsConstructor
public class ItemCreateRequest {
    private String title;

    private String description;

    private int price;

    private Long categoryId;
}
