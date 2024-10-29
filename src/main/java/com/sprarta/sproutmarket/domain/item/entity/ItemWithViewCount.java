package com.sprarta.sproutmarket.domain.item.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ItemWithViewCount {

    private Item item;
    private Long viewCount;

}
