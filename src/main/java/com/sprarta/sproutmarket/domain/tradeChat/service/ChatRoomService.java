package com.sprarta.sproutmarket.domain.tradeChat.service;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.tradeChat.dto.ChatRoomDto;
import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import com.sprarta.sproutmarket.domain.tradeChat.repository.ChatRoomRepository;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    // 채팅방 생성
    @Transactional
    public ChatRoomDto createChatRoom(Long itemId, CustomUserDetails authUser) {
        User buyer = findUserById(authUser.getId());
        Item item = itemRepository.findByIdOrElseThrow(itemId);

        // 상품과 구매자 사이에 기존 채팅방이 있는지 확인
        Optional<ChatRoom> findChatRoom = chatRoomRepository.findByItemAndBuyer(item, buyer);
        if (findChatRoom.isPresent()) {
            throw new ApiException(ErrorStatus.CONFLICT_CHATROOM);
        }

        // 상품 판매자와 현재 사용자 id 가 동일할 경우 생성 X
        if (ObjectUtils.nullSafeEquals(item.getSeller().getId(), buyer.getId())) {
            throw new ApiException(ErrorStatus.FORBIDDEN_CHATROOM_CREATE);
        }

        chatRoomRepository.save(new ChatRoom(
                buyer,
                item.getSeller(),
                item
        ));

        return new ChatRoomDto(
                buyer.getId(),
                item.getSeller().getId(),
                item.getId()
        );
    }

    // 채팅방 조회
    public ChatRoomDto getChatRoom(Long chatRoomId, CustomUserDetails authUser) {
        User user = findUserById(authUser.getId());
        ChatRoom chatRoom = chatRoomRepository.findByIdOrElseThrow(chatRoomId);
        chatRoomMatch(chatRoom, user.getId());

        return new ChatRoomDto(
                chatRoom.getBuyer().getId(),
                chatRoom.getSeller().getId(),
                chatRoom.getItem().getId()
        );
    }

    // 채팅방 삭제
    @Transactional
    public void deleteChatRoom(Long chatRoomId, CustomUserDetails authUser) {
        User user = findUserById(authUser.getId());
        ChatRoom chatRoom = chatRoomRepository.findByIdOrElseThrow(chatRoomId);
        chatRoomMatch(chatRoom, user.getId());

        chatRoomRepository.delete(chatRoom);
    }

    // 사용자 소속 채팅방 전체 조회
    public Page<ChatRoomDto> getChatRooms(CustomUserDetails authUser, Pageable pageable) {
        User user = findUserById(authUser.getId());

        return chatRoomRepository.
                findAllByUserId(user.getId(),pageable)
                .map(ChatRoomDto::new);
    }

    // 사용자 존재 확인 - > 유저 레포지토리 에서 처리할 것
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_USER));
    }

    // 채팅방에 소속된 사용자(구매자 , 판매자) id 와 현재 사용자 id 일치 여부 확인
    private void chatRoomMatch(ChatRoom chatRoom, Long userId) {
        if (!ObjectUtils.nullSafeEquals(chatRoom.getBuyer().getId(), userId)
        && !ObjectUtils.nullSafeEquals(chatRoom.getSeller().getId(), userId)) {
            throw new ApiException(ErrorStatus.FORBIDDEN_NOT_OWNED_CHATROOM);
        }
    }

}
