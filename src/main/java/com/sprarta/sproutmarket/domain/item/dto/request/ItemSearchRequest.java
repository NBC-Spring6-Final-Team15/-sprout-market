package com.sprarta.sproutmarket.domain.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchRequest {
    private String searchKeyword;
    private Long categoryId;
    private boolean saleStatus = false;
}
