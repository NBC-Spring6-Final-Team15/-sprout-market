package com.sprarta.sproutmarket.domain.item.controller;

import com.sprarta.sproutmarket.domain.item.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ItemWithViewCount {

    private Item item;
    private Long viewCount;

}
