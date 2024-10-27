package com.sprarta.sproutmarket.domain.tradeChat.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.tradeChat.dto.ChatRequest;
import com.sprarta.sproutmarket.domain.tradeChat.dto.ChatResponse;
import com.sprarta.sproutmarket.domain.tradeChat.service.ChatService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/chatrooms/{chatroomId}/chats")
    public ResponseEntity<ApiResponse<ChatResponse>> createChat(
            @PathVariable Long chatroomId, @RequestBody ChatRequest chatRequest,
            @AuthenticationPrincipal CustomUserDetails authUser) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                chatService.createChat(chatroomId, chatRequest, authUser)));
    }

    @GetMapping("/chatrooms/{chatroomId}/chats")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getChats(
            @PathVariable Long chatroomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                chatService.getChats(chatroomId, userDetails)
        ));
    }

    @PutMapping("/chats/{chatId}")
    public ResponseEntity<ApiResponse<ChatResponse>> deleteChat(
            @PathVariable Long chatId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                chatService.deleteChat(chatId, userDetails)
        ));
    }

}
