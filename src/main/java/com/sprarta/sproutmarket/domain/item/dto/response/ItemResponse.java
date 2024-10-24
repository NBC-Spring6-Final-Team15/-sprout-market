package com.sprarta.sproutmarket.domain.item.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 아닌값만 출력됨
public class ItemResponse {
    private String title;
    private String description;
    private int price;
    private ItemSaleStatus itemSaleStatus;
    private Status status;
    private String nickname;

    public ItemResponse(String title, int price, String nickname) {
        this.title = title;
        this.price = price;
        this.nickname = nickname;
    }

    public ItemResponse(String title, String description, int price, String nickname) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.nickname = nickname;
    }

    public ItemResponse(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public ItemResponse(String title, Status status, String nickname) {
        this.title = title;
        this.status = status;
        this.nickname = nickname;
    }

    public ItemResponse(String title, int price, ItemSaleStatus itemSaleStatus, String nickname) {
        this.title = title;
        this.price = price;
        this.itemSaleStatus = itemSaleStatus;
        this.nickname = nickname;
    }
}
