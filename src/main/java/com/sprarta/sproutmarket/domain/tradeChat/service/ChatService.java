package com.sprarta.sproutmarket.domain.tradeChat.service;

import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.tradeChat.dto.ChatRequest;
import com.sprarta.sproutmarket.domain.tradeChat.dto.ChatResponse;
import com.sprarta.sproutmarket.domain.tradeChat.entity.Chat;
import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import com.sprarta.sproutmarket.domain.tradeChat.repository.ChatRepository;
import com.sprarta.sproutmarket.domain.tradeChat.repository.ChatRoomRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatResponse createChat(Long chatroomId, ChatRequest chatRequest, CustomUserDetails userDetails) {
        User sender = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_CHATROOM));

        if (!ObjectUtils.nullSafeEquals(chatRoom.getBuyer().getId(), sender.getId())
                && !ObjectUtils.nullSafeEquals(chatRoom.getSeller().getId(), sender.getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_NOT_OWNED_CHATROOM);
        }

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(chatRequest.getContent())
                .status(Status.ACTIVE)
                .build();

        chatRepository.save(chat);

        return new ChatResponse(
                chat.getSender().getNickname(),
                chat.getContent()
        );
    }

    public List<ChatResponse> getChats(Long chatroomId, CustomUserDetails userDetails) {
        User sender = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_CHATROOM));

        if (!ObjectUtils.nullSafeEquals(chatRoom.getBuyer().getId(), sender.getId())
                && !ObjectUtils.nullSafeEquals(chatRoom.getSeller().getId(), sender.getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_NOT_OWNED_CHATROOM);
        }

        List<ChatResponse> chatDtoList = new ArrayList<>();

        for (Chat chat : chatRepository.findByChatroomId(chatroomId)) {
            String content = (chat.getStatus() == Status.DELETED) ? "삭제된 댓글입니다." : chat.getContent();

            ChatResponse chatDto = new ChatResponse(
                    chat.getSender().getNickname(),
                    content
            );
            chatDtoList.add(chatDto);
        }

        return chatDtoList;
    }

    public ChatResponse deleteChat(Long chatId, CustomUserDetails userDetails) {
        User sender = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));
        Chat chat = chatRepository.findByIdOrElseThrow(chatId);

        if (!ObjectUtils.nullSafeEquals(chat.getSender().getId(), sender.getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_NOT_OWNED_CHAT);
        }

        chat.chatDelete(Status.DELETED);

        chatRepository.save(chat);

        return new ChatResponse(
                chat.getSender().getNickname(),
                chat.getContent()
        );
    }

}
