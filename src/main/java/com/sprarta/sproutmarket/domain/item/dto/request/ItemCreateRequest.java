package com.sprarta.sproutmarket.domain.item.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCreateRequest {
    private String title;
    private String description;
    private int price;
    private Long categoryId;
    private String imageName;

    @Builder
    private ItemCreateRequest(String title, String description, int price, Long categoryId, String imageName) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.imageName = imageName;
    }
}
