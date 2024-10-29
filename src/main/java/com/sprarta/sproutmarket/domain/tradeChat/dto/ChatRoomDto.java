package com.sprarta.sproutmarket.domain.tradeChat.dto;

import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.user.entity.User;
import lombok.Getter;

@Getter
public class ChatRoomDto {

    private Long buyerId;
    private Long sellerId;
    private Long itemId;

    public ChatRoomDto(Long buyerId, Long sellerId, Long itemId) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.itemId = itemId;
    }

}
