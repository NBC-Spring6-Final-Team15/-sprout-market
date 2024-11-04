package com.sprarta.sproutmarket.domain.chat.service;

import com.sprarta.sproutmarket.domain.category.entity.Category;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.item.repository.ItemRepository;
import com.sprarta.sproutmarket.domain.tradeChat.dto.TradeChatDto;
import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import com.sprarta.sproutmarket.domain.tradeChat.entity.TradeChat;
import com.sprarta.sproutmarket.domain.tradeChat.repository.ChatRoomRepository;
import com.sprarta.sproutmarket.domain.tradeChat.repository.TradeChatRepository;
import com.sprarta.sproutmarket.domain.tradeChat.service.TradeChatService;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TradeChatServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private TradeChatRepository tradeChatRepository;
    @InjectMocks
    private TradeChatService tradeChatService;

    private User buyer;
    private User seller;
    private Category mockCategory;
    private Item mockItem1;
    private ChatRoom mockChatRoom1;
    private TradeChat mockChat1;
    private TradeChat mockChat2;
    private TradeChat mockChat3;
    private CustomUserDetails authUser;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

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

        mockCategory = new Category("카테고리");
        ReflectionTestUtils.setField(mockCategory, "id", 1L);

        mockItem1 = new  Item(
                "상품1",
                "설명1",
                10000,
                buyer,
                ItemSaleStatus.WAITING,
                mockCategory,
                Status.ACTIVE);
        ReflectionTestUtils.setField(mockItem1, "id", 1L);

        mockChatRoom1 = new ChatRoom(
                buyer,
                seller,
                mockItem1
        );
        ReflectionTestUtils.setField(mockChatRoom1, "id", 1L);

        mockChat1 = new TradeChat(
                buyer.getNickname(),
                "content1",
                mockChatRoom1.getId()
        );
        mockChat2 = new TradeChat(
                buyer.getNickname(),
                "content2",
                mockChatRoom1.getId()
        );
        mockChat3 = new TradeChat(
                buyer.getNickname(),
                "content3",
                mockChatRoom1.getId()
        );

        authUser = mock(CustomUserDetails.class);
        when(authUser.getId()).thenReturn(buyer.getId()); // authUser의 ID를 buyer의 ID로 설정
        when(authUser.getEmail()).thenReturn("email1@email.com");

    }

    @Test
    void 채팅_저장_성공() {

        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(itemRepository.findByIdOrElseThrow(mockItem1.getId())).thenReturn(mockItem1);
        when(chatRoomRepository.findByIdOrElseThrow(mockChatRoom1.getId())).thenReturn(mockChatRoom1);

        TradeChatDto tradeChatDto = new TradeChatDto(
                mockChatRoom1.getId(),
                buyer.getNickname(),
                "content"
        );

        tradeChatService.saveChat(tradeChatDto);

        verify(tradeChatRepository).save(any(TradeChat.class));

    }

    @Test
    void 채팅_조회_성공() {

        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(itemRepository.findByIdOrElseThrow(mockItem1.getId())).thenReturn(mockItem1);
        when(chatRoomRepository.findByIdOrElseThrow(mockChatRoom1.getId())).thenReturn(mockChatRoom1);
        when(tradeChatRepository.findAllByroomId(mockChatRoom1.getId())).thenReturn(List.of(mockChat1, mockChat2, mockChat3));

        List<TradeChatDto> chats = tradeChatService.getChats(mockChatRoom1.getId(), authUser);

        assertEquals(3, chats.size());

    }

}
