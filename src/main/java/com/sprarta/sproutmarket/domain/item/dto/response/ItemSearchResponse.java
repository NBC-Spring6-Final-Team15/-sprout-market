package com.sprarta.sproutmarket.domain.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ItemSearchResponse {
    private Long id;
    private String title;
    private int price;
    private String address;
    private String imageUrl;
    private LocalDateTime createAt;
}
