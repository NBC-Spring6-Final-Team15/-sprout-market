package com.sprarta.sproutmarket.domain.chat.service;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.tradeChat.dto.ChatRoomDto;
import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import com.sprarta.sproutmarket.domain.tradeChat.repository.ChatRoomRepository;
import com.sprarta.sproutmarket.domain.tradeChat.service.ChatRoomService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ChatRoomServiceTest {

    // 가짜 객체 사용
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @InjectMocks
    private ChatRoomService chatRoomService;

    private User buyer;
    private User seller;
    private User mockUser;
    private Category mockCategory;
    private Item mockItem1;
    private Item mockItem2;
    private ChatRoom mockChatRoom1;
    private ChatRoom mockChatRoom2;
    private CustomUserDetails authUser;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this); //어노테이션 Mock과 InjectMocks를 초기화

        // 가짜 사용자 생성
        buyer = new User(
                "user1",
                "email1@email.com",
                "pass1234!",
                "nick1",
                "01012341234",
                "address here",
                UserRole.USER
        );
        ReflectionTestUtils.setField(buyer, "id", 1L);

        seller = new User(
                "user2",
                "email2@email.com",
                "pass1234!",
                "nick2",
                "01012345678",
                "address here2",
                UserRole.USER
        );
        ReflectionTestUtils.setField(seller, "id", 2L);

        mockUser = new User(
                "user3",
                "email3@email.com",
                "pass1234!",
                "nick3",
                "01087654321",
                "address here3",
                UserRole.USER
        );
        ReflectionTestUtils.setField(mockUser, "id", 3L);

        // 가짜 카테고리 생성
        mockCategory = new Category("나무 상품");
        ReflectionTestUtils.setField(mockCategory, "id", 1L);

        // 가짜 상품 생성
        mockItem1 = new Item(
            "상품1",
            "설명1",
            10000,
            buyer,
            mockCategory
        );

        ReflectionTestUtils.setField(mockItem1, "id", 1L);

        mockItem2 = new Item(
            "상품2",
            "설명2",
            5000,
            seller,
            mockCategory
        );
        ReflectionTestUtils.setField(mockItem2, "id", 2L);

        mockChatRoom1 = new ChatRoom(
                buyer,
                seller,
                mockItem1
        );
        ReflectionTestUtils.setField(mockChatRoom1, "id", 1L);

        mockChatRoom2 = new ChatRoom(
                buyer,
                seller,
                mockItem2
        );
        ReflectionTestUtils.setField(mockChatRoom2, "id", 2L);

        // CustomUserDetails(사용자 정보) 모킹 => 로그인된 사용자의 정보 모킹
        authUser = mock(CustomUserDetails.class);
        when(authUser.getId()).thenReturn(buyer.getId()); // authUser의 ID를 buyer의 ID로 설정
        when(authUser.getEmail()).thenReturn("email1@email.com");

    }


    @Test
    void 채팅방_생성_성공() {

        // Given
        // 구매자가 상품2에 채팅방 생성
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(itemRepository.findByIdOrElseThrow(mockItem2.getId())).thenReturn(mockItem2);
        when(chatRoomRepository.findByItemAndBuyer(mockItem2, buyer)).thenReturn(Optional.empty());

        // When
        ChatRoomDto chatRoomDto = chatRoomService.createChatRoom(mockItem2.getId(), authUser);

        // Then
        assertEquals(buyer.getId(), chatRoomDto.getBuyerId());
        assertEquals(seller.getId(), chatRoomDto.getSellerId());
        assertEquals(mockItem2.getId(), chatRoomDto.getItemId());
        verify(chatRoomRepository).save(any(ChatRoom.class));

    }

    @Test
    void 채팅방_생성_실패__이미_생성된_채팅방() {

        // Given
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(itemRepository.findByIdOrElseThrow(mockItem2.getId())).thenReturn(mockItem2);
        when(chatRoomRepository.findByItemAndBuyer(mockItem2, buyer)).thenReturn(Optional.of(new ChatRoom(buyer, seller, mockItem2)));

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () ->
                chatRoomService.createChatRoom(mockItem2.getId(), authUser));
        assertEquals(ErrorStatus.CONFLICT_CHATROOM, exception.getErrorCode());
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));

    }

    @Test
    void 채팅방_생성_실패__자신의_상품에_생성() {

        // Given
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(itemRepository.findByIdOrElseThrow(mockItem1.getId())).thenReturn(mockItem1);
        when(chatRoomRepository.findByItemAndBuyer(mockItem1, buyer)).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () ->
                chatRoomService.createChatRoom(mockItem1.getId(), authUser));
        assertEquals(ErrorStatus.FORBIDDEN_CHATROOM_CREATE, exception.getErrorCode());
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));

    }

    @Test
    void 채팅방_조회_성공() {

        // Given
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(itemRepository.findByIdOrElseThrow(mockItem2.getId())).thenReturn(mockItem2);
        when(chatRoomRepository.findByIdOrElseThrow(mockChatRoom2.getId())).thenReturn(mockChatRoom2);

        // When
        ChatRoomDto chatRoomDto = chatRoomService.getChatRoom(mockChatRoom2.getId(), authUser);

        // Then
        assertEquals(buyer.getId(), chatRoomDto.getBuyerId());
        assertEquals(seller.getId(), chatRoomDto.getSellerId());
        assertEquals(mockItem2.getId(), chatRoomDto.getItemId());

    }

    @Test
    void 채팅방_목록_조회_성공() {

        Pageable pageable = PageRequest.of(0, 20);
        Page<ChatRoom> chatRoomPage = new PageImpl<>(List.of(mockChatRoom1, mockChatRoom2), pageable, 2);

        // Given
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(itemRepository.findByIdOrElseThrow(mockItem1.getId())).thenReturn(mockItem1);
        when(itemRepository.findByIdOrElseThrow(mockItem2.getId())).thenReturn(mockItem2);
        when(chatRoomRepository.findByIdOrElseThrow(mockChatRoom1.getId())).thenReturn(mockChatRoom1);
        when(chatRoomRepository.findByIdOrElseThrow(mockChatRoom2.getId())).thenReturn(mockChatRoom2);
        when(chatRoomRepository.findAllByUserId(buyer.getId(), pageable)).thenReturn(chatRoomPage);

        // When
        Page<ChatRoomDto> chatRooms = chatRoomService.getChatRooms(authUser, pageable);

        // Then
        assertEquals(2, chatRooms.getContent().size()); // 페이지의 콘텐츠 크기 확인
        assertEquals(1, chatRooms.getTotalPages()); // 총 페이지 수 확인

    }

    @Test
    void 채팅방_삭제_성공() {

        // Given
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(itemRepository.findByIdOrElseThrow(mockItem2.getId())).thenReturn(mockItem2);
        when(chatRoomRepository.findByIdOrElseThrow(mockChatRoom2.getId())).thenReturn(mockChatRoom2);

        // When
        chatRoomService.deleteChatRoom(mockChatRoom2.getId(), authUser);

        // Then
        verify(chatRoomRepository).delete(mockChatRoom2);

    }

    @Test
    void 채팅방_삭제_실패__채팅방_소속_아님() {

        // Given
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(itemRepository.findByIdOrElseThrow(mockItem2.getId())).thenReturn(mockItem2);
        when(chatRoomRepository.findByIdOrElseThrow(mockChatRoom2.getId())).thenReturn(mockChatRoom2);

        authUser = mock(CustomUserDetails.class);
        when(authUser.getId()).thenReturn(mockUser.getId());
        when(authUser.getEmail()).thenReturn("email3@email.com");

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () ->
                chatRoomService.deleteChatRoom(mockChatRoom2.getId(), authUser));
        assertEquals(ErrorStatus.FORBIDDEN_NOT_OWNED_CHATROOM, exception.getErrorCode());
        verify(chatRoomRepository, never()).delete(mockChatRoom2);

    }

}
