package com.sprarta.sproutmarket.domain.tradeChat.dto;

import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatReadStatus;
import lombok.Getter;

@Getter
public class TradeChatResponse {

    private String content; // 메시지 내용
    private ChatReadStatus chatReadStatus; // 읽음 상태

    public TradeChatResponse(String content, ChatReadStatus chatReadStatus) {
        this.content = content;
        this.chatReadStatus = chatReadStatus;
    }

}
