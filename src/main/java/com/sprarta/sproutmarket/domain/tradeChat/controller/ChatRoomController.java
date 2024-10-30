package com.sprarta.sproutmarket.domain.tradeChat.controller;

import com.sprarta.sproutmarket.domain.common.ApiResponse;
import com.sprarta.sproutmarket.domain.tradeChat.dto.ChatRoomDto;
import com.sprarta.sproutmarket.domain.tradeChat.service.ChatRoomService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅 저장 , 방식 결정에 따라 이후 수정 예정

    // 채팅방 생성
    @PostMapping("/items/{itemId}/chatrooms")
    public ResponseEntity<ApiResponse<ChatRoomDto>> createChatRoom(
            @PathVariable("itemId") Long itemId, @AuthenticationPrincipal CustomUserDetails authUser) {
        ChatRoomDto chatRoomDto = chatRoomService.createChatRoom(
                itemId, authUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess("Created",201, chatRoomDto));
    }

    // 채팅방 조회
    @GetMapping("/chatrooms/{chatroomId}")
    public ResponseEntity<ApiResponse<ChatRoomDto>> getChatRoom(
            @PathVariable("chatroomId") Long chatroomId, @AuthenticationPrincipal CustomUserDetails authUser) {
        ChatRoomDto chatRoomDto = chatRoomService.getChatRoom(
                chatroomId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(chatRoomDto));
    }

    // 모든 채팅방 목록 조회
    @GetMapping("/chatrooms")
    public ResponseEntity<ApiResponse<List<ChatRoomDto>>> getAllChatRooms(
            @AuthenticationPrincipal CustomUserDetails authUser) {
        List<ChatRoomDto> chatRoomDtoList = chatRoomService.getAllChatRooms(authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(chatRoomDtoList));
    }

    // 채팅방 삭제
    @DeleteMapping("/chatrooms/{chatroomId}")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(
            @PathVariable("chatroomId") Long chatroomId, @AuthenticationPrincipal CustomUserDetails authUser) {
        chatRoomService.deleteChatRoom(chatroomId, authUser);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

}
