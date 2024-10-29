package com.sprarta.sproutmarket.domain.tradeChat.dto;

import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatReadStatus;
import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import lombok.Getter;

@Getter
public class TradeChatRequest {

    private String content;
    private ChatReadStatus chatReadStatus; // 읽음 상태

    private ChatRoom chatRoom;

}
