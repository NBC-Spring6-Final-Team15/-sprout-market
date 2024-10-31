package com.sprarta.sproutmarket.domain.tradeChat.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.tradeChat.dto.TradeChatDto;
import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import com.sprarta.sproutmarket.domain.tradeChat.entity.TradeChat;
import com.sprarta.sproutmarket.domain.tradeChat.repository.TradeChatRepository;
import com.sprarta.sproutmarket.domain.tradeChat.service.TradeChatService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class TradeChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final TradeChatService tradeChatService;

    @MessageMapping("/chat/{roomId}")
    public void message(TradeChatDto tradeChatDto,
                        @DestinationVariable("roomId") Long roomId) {
        messagingTemplate.convertAndSend("/sub/chat/" + roomId, tradeChatDto);

        tradeChatService.saveChat(tradeChatDto);
    }

    @GetMapping("/chatRoom/{roomId}/chats")
    public ResponseEntity<ApiResponse<List<TradeChatDto>>> getChats(@PathVariable("roomId") Long roomId){
        return ResponseEntity.ok(ApiResponse.onSuccess((tradeChatService.getChats(roomId))));
    }

}