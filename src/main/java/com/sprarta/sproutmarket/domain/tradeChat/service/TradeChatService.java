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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final TradeChatRepository tradeChatRepository;

    public void saveChat(TradeChatDto tradeChatDto) {
        tradeChatRepository.save(new TradeChat(
                tradeChatDto.getSender(),
                tradeChatDto.getContent(),
                tradeChatDto.getRoomId()));
    }

    public List<TradeChatDto> getChats(Long chatroomId) {
        chatRoomRepository.findByIdOrElseThrow(chatroomId);

        List<TradeChatDto> tradeChatDtoList = new ArrayList<>();

        for (TradeChat tradeChat : tradeChatRepository.findAllByroomId(chatroomId)) {
            TradeChatDto tradeChatDto = new TradeChatDto(
                    tradeChat.getRoomId(),
                    tradeChat.getSender(),
                    tradeChat.getContent()
            );
            tradeChatDtoList.add(tradeChatDto);
        }

        return tradeChatDtoList;
    }

}
