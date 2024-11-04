package com.sprarta.sproutmarket.domain.item.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 아닌값만 출력됨
public class ItemResponse {
    private String title;
    private String description;
    private int price;
    private ItemSaleStatus itemSaleStatus;
    private Status status;
    private String nickname;
    private String imageUrl;

    @Builder
    private ItemResponse(String title, String description, int price, ItemSaleStatus itemSaleStatus, Status status, String nickname, String imageUrl) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.itemSaleStatus = itemSaleStatus;
        this.status = status;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }
}
