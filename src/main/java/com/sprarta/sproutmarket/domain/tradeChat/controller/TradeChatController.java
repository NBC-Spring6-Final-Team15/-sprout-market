package com.sprarta.sproutmarket.domain.tradeChat.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.tradeChat.dto.TradeChatDto;
import com.sprarta.sproutmarket.domain.tradeChat.service.TradeChatService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TradeChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final TradeChatService tradeChatService;

    @MessageMapping("/chat/{roomId}")
    public void message(TradeChatDto tradeChatDto,
                        @DestinationVariable("roomId") Long roomId) {
        tradeChatService.chatRoomMatch(tradeChatDto.getRoomId(), Long.parseLong(tradeChatDto.getSender()));

        messagingTemplate.convertAndSend("/sub/chat/" + roomId, tradeChatDto);

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