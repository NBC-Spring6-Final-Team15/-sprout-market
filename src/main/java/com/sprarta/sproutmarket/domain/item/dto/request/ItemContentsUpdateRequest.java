package com.sprarta.sproutmarket.domain.item.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemContentsUpdateRequest {
        private String title;
        private String description;
        private int price;

        @Builder
        private ItemContentsUpdateRequest(String title, String description, int price) {
                this.title = title;
                this.description = description;
                this.price = price;
        }
}
