package com.sprarta.sproutmarket.domain.item.entity;

import com.sprarta.sproutmarket.domain.item.exception.SaleStatusNotFoundException;

import java.util.Arrays;

public enum ItemSaleStatus {
    WAITING, RESERVED, SOLD;

    public static ItemSaleStatus of(String saleStatus){
        return Arrays.stream(ItemSaleStatus.values())
            .filter(r -> r.name().equalsIgnoreCase(saleStatus))
            .findFirst()
            .orElseThrow(() -> new SaleStatusNotFoundException("유효하지 않은 saleStatus"));
    }
}
