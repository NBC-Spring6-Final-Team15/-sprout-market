package com.sprarta.sproutmarket.domain.item.entity;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;

import java.util.Arrays;

public enum ItemSaleStatus {
    WAITING, RESERVED, SOLD;

    public static ItemSaleStatus of(String saleStatus){
        return Arrays.stream(ItemSaleStatus.values())
            .filter(r -> r.name().equalsIgnoreCase(saleStatus))
            .findFirst()
            .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_ITEM_SALE_STATUS));
    }
}
