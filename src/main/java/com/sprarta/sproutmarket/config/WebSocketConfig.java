package com.sprarta.sproutmarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry; // 메시지 브로커 구성 정의
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry; // STOMP 프로토콜의 엔드포인트 등록
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer; // 웹소켓 메시지 브로커 구성을 위한 인터페이스

@Configuration
@EnableWebSocketMessageBroker // 웹소켓 메시징 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {



    // 노출할 endpoint 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        // 웹소켓이 연결하는 endpoint
        stompEndpointRegistry.addEndpoint("/ws") // 연결 url 설정
                .setAllowedOriginPatterns("*") // 모든 출처에서의 요청을 허용 이후 보안상 변경할 것
                .withSockJS();

    }

    //메세지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        // 서버 -> 클라이언트로 발행하는 메세지에 대한 endpoint 설정 : 구독
        messageBrokerRegistry.enableSimpleBroker("/sub"); // 메시지 브로커 활성화

        // 클라이언트->서버로 발행하는 메세지에 대한 endpoint 설정 : 구독에 대한 메세지
        messageBrokerRegistry.setApplicationDestinationPrefixes("/pub"); // 메시지를 발행할 때 사용할 접두사
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(chatPreHandler);
//    }

}