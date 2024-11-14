package com.sprarta.sproutmarket.domain.tradeChat.service;

import com.sprarta.sproutmarket.domain.tradeChat.dto.TradeChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, Object> chatRedisTemplate;

    // Redis Topic 에 메시지 발행.  메시지를 발행 후, 대기 중이던 redis 구독 서비스(RedisSubscriber)가 메시지를 처리
    public void publish(Long roomId, TradeChatDto tradeChatDto) {
        ChannelTopic topic = new ChannelTopic("chat/" + roomId);

        // 발행되는 채널과 메시지 로그 출력
        System.out.println("### Redis에 발행하는 메시지 채널: " + topic.getTopic());
        System.out.println("### 발행된 메시지 내용: " + tradeChatDto);

        chatRedisTemplate.convertAndSend(topic.getTopic(), tradeChatDto);
    }

}
