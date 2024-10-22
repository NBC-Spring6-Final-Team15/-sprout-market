package com.sprarta.sproutmarket.domain.item.entity;

import org.apache.coyote.BadRequestException;

import java.util.Arrays;

public enum ItemSaleStatus {
    SAVED, RESERVED, SOLD_OUT;

    public static ItemSaleStatus of(String saleStatus) throws BadRequestException {
        return Arrays.stream(ItemSaleStatus.values())
            .filter(r -> r.name().equalsIgnoreCase(saleStatus))
            .findFirst()
            .orElseThrow(() -> new BadRequestException("유효하지 않은 saleStatus"));
    }
}
