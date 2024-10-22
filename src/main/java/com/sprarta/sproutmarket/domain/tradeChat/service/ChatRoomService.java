package com.sprarta.sproutmarket.domain.tradeChat.service;

import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    // 채팅방 생성
    public Long createRoom(Long itemId) {

        return itemId;
    }
    // 아이템 , 현재 사용자 받아와서 아이템id , 구매자 판매자 id 로 방 생성

}
