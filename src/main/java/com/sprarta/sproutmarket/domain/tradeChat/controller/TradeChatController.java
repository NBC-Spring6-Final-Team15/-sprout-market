package com.sprarta.sproutmarket.domain.tradeChat.controller;

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

@RequiredArgsConstructor
@Controller
public class TradeChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final TradeChatService tradeChatService;
    private final TradeChatRepository tradeChatRepository;

    @MessageMapping("/chat/{roomId}")
    public void message(TradeChatDto tradeChatDto,
                        @DestinationVariable("roomId") Long roomId) {

        ChatRoom chatRoom = tradeChatService.findChatRoom(roomId);
        tradeChatDto.setRoomId(roomId); // roomId 설정

        tradeChatService.saveChat(tradeChatDto);

        messagingTemplate.convertAndSend("/sub/chat/" + roomId, tradeChatDto);
        System.out.println("확인 용도");
    }

    @GetMapping("/chatRoom/{roomId}/chat")
    public ResponseEntity<TradeChatDto> getChat(@PathVariable("roomId") Long roomId){

        TradeChat tradeChat = tradeChatRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_CHATROOM));;
        TradeChatDto tradeChatDto = new TradeChatDto(
                tradeChat.getRoomId(),
                tradeChat.getSender(),
                tradeChat.getContent()
        );

        return ResponseEntity.ok(tradeChatDto);
    }

}