package com.sprarta.sproutmarket.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class ChatPreHandler implements ChannelInterceptor {
//
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        // ! 연결 요청시 JWT 검증
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            // ! Authorization 헤더 추출
//            String token = accessor.getFirstNativeHeader("Authorization");
//            log.info("token = {}", token);
//            if (token != null && token.startsWith("Bearer ")) {
//                token = token.substring(7); // ! 추출
//                try {
//                    // ! JWT 토큰 검증
//                    Map<String, Object> claims = jwtUtil.validDataToken(token);
//                    log.info("claims = {}", claims); // ! 전체 클레임 로깅
//                    Long memberId = Long.parseLong(claims.get("id").toString());
//                    log.info("memberId = {}", memberId);
//                    // 인증 정보를 세션 속성에 저장
//                    accessor.getSessionAttributes().put("AUTHENTICATED_MEMBER_ID", memberId);
//
//                    // 인증 객체 생성 및 설정
//                    UsernamePasswordAuthenticationToken auth =
//                            new UsernamePasswordAuthenticationToken(memberId, null, Collections.emptyList());
//                    accessor.setUser(auth);
//
//                    log.info("Authentication set for user: {}", memberId);
//                } catch (Exception e) {
//                    // ! 토큰 검증 실패
//                    log.error("토큰 유효하지 않음 or 실패", e);
//                    return null;
//                }
//            } else {
//                log.error("유효한 토큰을 찾지 못함");
//                return null; // ! 토큰 없거나 형식 잘못됨
//            }
//        } else {
//            // CONNECT 이외의 명령에 대해 인증 정보 확인
//            Object memberId = accessor.getSessionAttributes().get("AUTHENTICATED_MEMBER_ID");
//            if (memberId != null) {
//                log.info("Authenticated user for non-CONNECT message: {}", memberId);
//            } else {
//                log.error("유저가 인증되지 못함");
//                return null;
//            }
//        }
//
//        // ! 모든 경우에 대해 인증 통과
//        return message;
//
//    }
//
//}