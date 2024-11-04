package com.sprarta.sproutmarket.domain.item.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSearchRequest {
    private String searchKeyword;
    private Long categoryId;
    private boolean saleStatus = false;

    @Builder
    private ItemSearchRequest(String searchKeyword, Long categoryId, boolean saleStatus) {
        this.searchKeyword = searchKeyword;
        this.categoryId = categoryId;
        this.saleStatus = saleStatus;
    }
}
