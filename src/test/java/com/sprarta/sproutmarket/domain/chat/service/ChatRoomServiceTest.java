package com.sprarta.sproutmarket.domain.chat.service;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.entity.Status;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                "서울시 노원구 공릉동",
                UserRole.USER
        );
        ReflectionTestUtils.setField(buyer, "id", 1L);

        seller = new User(
                "user2",
                "email2@email.com",
                "pass1234!",
                "nick2",
                "01012345678",
                "서울시 노원구 공릉동",
                UserRole.USER
        );
        ReflectionTestUtils.setField(seller, "id", 2L);

        mockUser = new User(
                "user3",
                "email3@email.com",
                "pass1234!",
                "nick3",
                "01087654321",
                "서울시 노원구 공릉동",
                UserRole.USER
        );
        ReflectionTestUtils.setField(mockUser, "id", 3L);

        // 가짜 카테고리 생성
        mockCategory = new Category(1L, "나무 상품");

        // 가짜 상품 생성
        mockItem1 = Item.builder()
                .title("상품1")
                .description("설명1")
                .price(10000)
                .itemSaleStatus(ItemSaleStatus.WAITING)
                .seller(buyer)
                .category(mockCategory)
                .status(Status.ACTIVE)
                .build();
        ReflectionTestUtils.setField(mockItem1, "id", 1L);

        mockItem2 = Item.builder()
                .title("상품2")
                .description("설명2")
                .price(5000)
                .itemSaleStatus(ItemSaleStatus.WAITING)
                .seller(seller)
                .category(mockCategory)
                .status(Status.ACTIVE)
                .build();
        ReflectionTestUtils.setField(mockItem2, "id", 2L);

        // CustomUserDetails(사용자 정보) 모킹 => 로그인된 사용자의 정보 모킹
        authUser = mock(CustomUserDetails.class);
        when(authUser.getId()).thenReturn(buyer.getId()); // authUser의 ID를 mockUser1의 ID로 설정
        when(authUser.getEmail()).thenReturn("email1@email.com");
        when(authUser.getRole()).thenReturn(UserRole.USER);

        // itemRepository.save() 호출 시 mockItem2를 반환하도록 설정
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem2);

        // userRepository.findById() 호출 시 mockUser를 반환하도록 설정
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));

        // itemRepository.findById() 호출 시 mockItem1과 mockItem2를 반환하도록 설정
        when(itemRepository.findById(mockItem1.getId())).thenReturn(Optional.of(mockItem1));
        when(itemRepository.findById(mockItem2.getId())).thenReturn(Optional.of(mockItem2));
    }


    @Test
    void 채팅방_생성_성공() {

        // Given


        // 구매자가 상품2에 채팅방 생성
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(itemRepository.findById(mockItem2.getId())).thenReturn(Optional.of(mockItem2));
        when(chatRoomRepository.findByItemAndBuyer(mockItem2, buyer)).thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        ChatRoomDto chatRoomDto = chatRoomService.createChatRoom(mockItem2.getId(), authUser);

        assertEquals(buyer.getId(), chatRoomDto.getBuyerId());
        assertEquals(seller.getId(), chatRoomDto.getSellerId());
        assertEquals(mockItem2.getId(), chatRoomDto.getItemId());

        verify(chatRoomRepository).save(any(ChatRoom.class));

    }

    @Test
    void 채팅방_이미_생성() {

    }

    @Test
    void 자신의_상품에_생성() {

    }

    @Test
    void 채팅방_조회_성공() {

    }

    @Test
    void 채팅방_목록_조회_성공() {

    }

    @Test
    void 채팅방_삭제_성공() {

    }

    @Test
    void 채팅방_소속_아님() {

    }


}
