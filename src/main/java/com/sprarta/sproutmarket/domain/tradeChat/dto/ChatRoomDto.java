package com.sprarta.sproutmarket.domain.tradeChat.dto;

import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
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

    public ChatRoomDto(ChatRoom chatRoom) {
        this.buyerId = chatRoom.getBuyer().getId();
        this.sellerId = chatRoom.getSeller().getId();
        this.itemId = chatRoom.getItem().getId();
    }

}
