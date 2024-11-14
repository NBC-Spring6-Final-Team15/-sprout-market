package com.sprarta.sproutmarket.domain.tradeChat.service;

import com.sprarta.sproutmarket.domain.tradeChat.dto.TradeChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSubscriber {

    private final SimpMessagingTemplate messagingTemplate;

    // Redis에서 수신하여 WebSocket 클라이언트로 전송
    public void handleMessage(TradeChatDto tradeChatDto) {
        System.out.println("Redis로부터 받은 메시지: " + tradeChatDto);
        messagingTemplate.convertAndSend("/sub/chat/" + tradeChatDto.getRoomId(), tradeChatDto);
    }

}