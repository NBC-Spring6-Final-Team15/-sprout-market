package com.sprarta.sproutmarket.domain.notification.entity;

import lombok.Getter;

@Getter
public class PriceChangeEvent {
    private final Long itemId;
    private final int newPrice;

    public PriceChangeEvent(Long itemId, int newPrice) {
        this.itemId = itemId;
        this.newPrice = newPrice;
    }
}
