package com.sprarta.sproutmarket.domain.tradeChat.dto;

import lombok.Getter;

@Getter
public class ChatResponse {

    private String sender; // 채팅의 경우 사용자의 닉네임을 표시
    private String content;

    public ChatResponse(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

}
