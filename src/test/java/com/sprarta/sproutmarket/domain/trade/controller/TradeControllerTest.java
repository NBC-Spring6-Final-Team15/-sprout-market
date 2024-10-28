package com.sprarta.sproutmarket.domain.trade.controller;


import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.config.SecurityConfig;
import com.sprarta.sproutmarket.domain.review.controller.ReviewController;
import com.sprarta.sproutmarket.domain.review.dto.ReviewRequestDto;
import com.sprarta.sproutmarket.domain.review.dto.ReviewResponseDto;
import com.sprarta.sproutmarket.domain.review.enums.ReviewRating;
import com.sprarta.sproutmarket.domain.review.service.ReviewService;
import com.sprarta.sproutmarket.domain.trade.dto.TradeRequestDto;
import com.sprarta.sproutmarket.domain.trade.dto.TradeResponseDto;
import com.sprarta.sproutmarket.domain.trade.enums.TradeStatus;
import com.sprarta.sproutmarket.domain.trade.service.TradeService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.service.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TradeController.class)
@Import(SecurityConfig.class)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
public class TradeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TradeService tradeService;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    CustomUserDetailService customUserDetailService;

    @BeforeEach
    void setUp() {

        CustomUserDetails mockAuthUser = new CustomUserDetails(
                new User(1L, "username",
                        "email@example.com",
                        "encodedOldPassword",
                        "nickname",
                        "010-1234-5678",
                        "address", UserRole.USER)
        );

        // 인증 유저 시큐리티 컨텍스트 홀더에 저장
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void 거래_예약_성공() throws Exception {
        // given
        Long itemId = 1L;
        TradeRequestDto tradeRequestDto = new TradeRequestDto(3L);
        TradeResponseDto tradeResponseDto = new TradeResponseDto(1L, 1L, "판매자 이름", "구매자 이름", TradeStatus.RESERVED);

        when(tradeService.reserveTrade(any(Long.class), any(TradeRequestDto.class), any(CustomUserDetails.class)))
                .thenReturn(tradeResponseDto);

        // when, then
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/trades/reservations/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tradeRequestDto))
                .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "reserve-trade",
                        resource(ResourceSnippetParameters.builder()
                                .description("새로운 거래 예약을 생성합니다.")
                                .pathParameters(
                                        parameterWithName("itemId").description("아이템 ID")
                                )
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .summary("거래 예약 생성")
                                .tag("Trade")
                                .requestFields(List.of(
                                        fieldWithPath("buyerId").description("구매자 ID")
                                ))
                                .responseFields(List.of(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                        fieldWithPath("data.id").description("거래 ID"),
                                        fieldWithPath("data.itemId").description("아이템 ID"),
                                        fieldWithPath("data.sellerName").description("판매자 이름"),
                                        fieldWithPath("data.buyerName").description("구매자 이름"),
                                        fieldWithPath("data.tradeStatus").description("거래 상태")
                                ))
                                .responseHeaders(
                                        headerWithName("Content-Type").description("응답의 Content-Type")
                                )
                                .build()
                        )
                ));
                result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(tradeResponseDto.getId()))
                .andExpect(jsonPath("$.data.itemId").value(tradeResponseDto.getItemId()));
    }

    @Test
    void 거래_완료_성공() throws Exception {
// given
        Long itemId = 1L;
        TradeRequestDto tradeRequestDto = new TradeRequestDto(3L);
        TradeResponseDto tradeResponseDto = new TradeResponseDto(1L, 1L, "판매자 이름", "구매자 이름", TradeStatus.RESERVED);

        when(tradeService.finishTrade(any(Long.class), any(TradeRequestDto.class), any(CustomUserDetails.class)))
                .thenReturn(tradeResponseDto);

        // when, then
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.put("/trades/completions/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tradeRequestDto))
                .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "complete-trade",
                        resource(ResourceSnippetParameters.builder()
                                .description("거래를 완료 상태로 변경합니다.")
                                .pathParameters(
                                        parameterWithName("itemId").description("아이템 ID")
                                )
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .summary("거래 상태 완료로 변경")
                                .tag("Trade")
                                .requestFields(List.of(
                                        fieldWithPath("buyerId").description("구매자 ID")
                                ))
                                .responseFields(List.of(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                        fieldWithPath("data.id").description("거래 ID"),
                                        fieldWithPath("data.itemId").description("아이템 ID"),
                                        fieldWithPath("data.sellerName").description("판매자 이름"),
                                        fieldWithPath("data.buyerName").description("구매자 이름"),
                                        fieldWithPath("data.tradeStatus").description("거래 상태")
                                ))
                                .responseHeaders(
                                        headerWithName("Content-Type").description("응답의 Content-Type")
                                )
                                .build()
                        )
                ));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(tradeResponseDto.getId()))
                .andExpect(jsonPath("$.data.itemId").value(tradeResponseDto.getItemId()));

    }

}
