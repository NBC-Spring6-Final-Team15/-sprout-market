package com.sprarta.sproutmarket.domain.item.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSearchResponse {
    private Long id;
    private String title;
    private int price;
    private String address;
    private String imageUrl;
    private LocalDateTime createAt;

    @Builder
    public ItemSearchResponse(Long id, String title, int price, String address, String imageUrl, LocalDateTime createAt) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.address = address;
        this.imageUrl = imageUrl;
        this.createAt = createAt;
    }
}
