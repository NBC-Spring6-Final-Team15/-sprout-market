package com.sprarta.sproutmarket.domain.trade.controller;


import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.sprarta.sproutmarket.domain.CommonMockMvcControllerTestSetUp;
import com.sprarta.sproutmarket.domain.item.entity.Item;
import com.sprarta.sproutmarket.domain.item.entity.ItemSaleStatus;
import com.sprarta.sproutmarket.domain.trade.dto.TradeResponseDto;
import com.sprarta.sproutmarket.domain.trade.entity.Trade;
import com.sprarta.sproutmarket.domain.trade.enums.TradeStatus;
import com.sprarta.sproutmarket.domain.trade.service.TradeService;
import com.sprarta.sproutmarket.domain.tradeChat.entity.ChatRoom;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TradeController.class)
@AutoConfigureMockMvc(addFilters = false)
class TradeControllerTest extends CommonMockMvcControllerTestSetUp {
    @MockBean
    TradeService tradeService;

    private Trade trade;
    @Spy
    private User buyer;
    @Spy
    private User seller;
    @Spy
    private Item item;

    @BeforeEach
    void setUp() {

        CustomUserDetails mockAuthUser = new CustomUserDetails(
                new User("username",
                        "email@example.com",
                        "encodedOldPassword",
                        "nickname",
                        "010-1234-5678",
                        "address", UserRole.USER)
        );
        ReflectionTestUtils.setField(mockAuthUser, "id", 1L);

        ReflectionTestUtils.setField(buyer, "id", 1L);
        ReflectionTestUtils.setField(buyer, "nickname", "buyer");

        ReflectionTestUtils.setField(seller, "id", 2L);
        ReflectionTestUtils.setField(seller, "nickname", "seller");

        ReflectionTestUtils.setField(item, "id", 1L);
        ReflectionTestUtils.setField(item, "title", "아이템");
        ReflectionTestUtils.setField(item, "itemSaleStatus", ItemSaleStatus.WAITING);

        ChatRoom chatRoom = new ChatRoom(buyer, seller, item);
        ReflectionTestUtils.setField(chatRoom, "id", 1L);

        trade = new Trade(chatRoom);
        ReflectionTestUtils.setField(trade, "id", 1L);

        // 인증 유저 시큐리티 컨텍스트 홀더에 저장
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void 거래_예약_성공() throws Exception {
        // given
        TradeResponseDto responseDto = TradeResponseDto.from(trade);

        when(tradeService.reserveTrade(any(Long.class), any(CustomUserDetails.class)))
                .thenReturn(responseDto);

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/chat-rooms/{chatRoomId}/trades", 1L)
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "reserve-trade",
                        resource(ResourceSnippetParameters.builder()
                                .description("새로운 거래 예약을 생성합니다.")
                                .pathParameters(
                                        parameterWithName("chatRoomId").description("거래 예약을 생성할 채팅방 ID")
                                                .type(SimpleType.NUMBER)
                                )
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .summary("거래 예약 생성")
                                .tag("Trade")
                                .responseFields(
                                        fieldWithPath("message").description("성공 시 응답 메시지 : Created, 예외 시 예외 메시지"),
                                        fieldWithPath("statusCode").description("성공 시 응답 코드 : 201"),
                                        fieldWithPath("data").description("응답 본문"),
                                        fieldWithPath("data.id").description("거래 ID"),
                                        fieldWithPath("data.itemTitle").description("아이템 이름"),
                                        fieldWithPath("data.sellerName").description("판매자 이름"),
                                        fieldWithPath("data.buyerName").description("구매자 이름"),
                                        fieldWithPath("data.tradeStatus").description("거래 상태"),
                                        fieldWithPath("data.buyerId").description("구매자 ID"),
                                        fieldWithPath("data.sellerId").description("판매자 ID")
                                )
                                .build()
                        )
                ));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.data.itemTitle").value(responseDto.getItemTitle()))
                .andExpect(jsonPath("$.data.buyerId").value(responseDto.getBuyerId()))
                .andExpect(jsonPath("$.data.sellerId").value(responseDto.getSellerId()));

    }

    @Test
    void 거래_완료_성공() throws Exception {
        doNothing().when(tradeService).finishTrade(any(Long.class), any(CustomUserDetails.class),any(TradeStatus.class));

        // when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/trades/{tradeId}?tradeStatus=COMPLETED", 1L)
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "complete-trade",
                        resource(ResourceSnippetParameters.builder()
                                .description("거래를 완료/취소 상태로 변경합니다.")
                                .pathParameters(
                                        parameterWithName("tradeId").description("거래 ID")
                                                .type(SimpleType.NUMBER)
                                )
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .queryParameters(
                                        parameterWithName("tradeStatus").description("COMPLETE or CANCELLED")
                                )
                                .summary("거래 상태 완료/취소로 변경")
                                .tag("Trade")
                                .responseSchema(Schema.schema("거래_상태_변경_성공_응답"))
                                .build()
                        )
                ));

        result.andExpect(status().isOk());

    }
}
