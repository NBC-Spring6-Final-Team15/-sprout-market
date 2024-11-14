package com.sprarta.sproutmarket.domain.tradeChat.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.tradeChat.dto.TradeChatDto;
import com.sprarta.sproutmarket.domain.tradeChat.service.TradeChatService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TradeChatController {

    private final TradeChatService tradeChatService;

    @MessageMapping("/chat/{roomId}")
    @SendTo("/sub/chat/{roomId}")
    public void sendMessage(TradeChatDto tradeChatDto,
                            @DestinationVariable("roomId") Long roomId) {
        tradeChatService.chatRoomMatch(tradeChatDto.getRoomId(), Long.parseLong(tradeChatDto.getSender()));
        tradeChatService.publishChat(roomId, tradeChatDto);  // 메시지 저장 및 퍼블리시
        tradeChatService.saveChat(tradeChatDto);
    }

    @PostMapping("/chat/{roomId}/decreaseReadCount")
    public ResponseEntity<ApiResponse<Void>> decreaseReadCount(
            @PathVariable("roomId") Long roomId, @RequestBody String sender) {
        tradeChatService.decreaseReadCount(roomId, sender);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    @GetMapping("/chatRooms/{roomId}/chats")
    public ResponseEntity<ApiResponse<List<TradeChatDto>>> getChats(
            @PathVariable("roomId") Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(ApiResponse.onSuccess((tradeChatService.getChats(roomId, userDetails))));
    }

}