package com.sprarta.sproutmarket.domain.item.eto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemContentsUpdateRequest {
        private String title;

        private String description;

        private int price;
        private String imageUrl;
}
