package com.sprarta.sproutmarket.domain.tradeChat.service;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.tradeChat.dto.TradeChatDto;
import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import com.sprarta.sproutmarket.domain.tradeChat.entity.TradeChat;
import com.sprarta.sproutmarket.domain.tradeChat.repository.ChatRoomRepository;
import com.sprarta.sproutmarket.domain.tradeChat.repository.TradeChatRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class TradeChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final TradeChatRepository tradeChatRepository;

    public ChatRoom findChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_CHATROOM));
        return chatRoom;
    }

    public void chatRoomMatch(ChatRoom chatRoom, Long userId) {
        if (!ObjectUtils.nullSafeEquals(chatRoom.getBuyer().getId(), userId)
                && !ObjectUtils.nullSafeEquals(chatRoom.getSeller().getId(), userId)) {
            throw new ApiException(ErrorStatus.FORBIDDEN_NOT_OWNED_CHATROOM);
        }
    }

    public void saveChat(TradeChatDto tradeChatDto) {
        TradeChat tradeChat = new TradeChat(
                tradeChatDto.getSender(),
                tradeChatDto.getContent(),
                tradeChatDto.getRoomId());
        tradeChatRepository.save(tradeChat);
    }




}
