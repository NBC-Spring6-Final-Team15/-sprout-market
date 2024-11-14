package com.sprarta.sproutmarket.domain.coupon.controller;


import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.sprarta.sproutmarket.domain.CommonMockMvcControllerTestSetUp;
import com.sprarta.sproutmarket.domain.coupon.dto.CouponResponseDto;
import com.sprarta.sproutmarket.domain.coupon.service.CouponService;
import com.sprarta.sproutmarket.domain.review.controller.ReviewController;
import com.sprarta.sproutmarket.domain.review.dto.ReviewRequestDto;
import com.sprarta.sproutmarket.domain.trade.enums.TradeStatus;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CouponController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CouponControllerTest extends CommonMockMvcControllerTestSetUp {

    @MockBean
    CouponService couponService;

    @BeforeEach
    void setUp() {
        User mockUser = new User( "username", "email@example.com", "encodedOldPassword", "nickname", "010-1234-5678", "address", UserRole.ADMIN);
        ReflectionTestUtils.setField(mockUser, "id", 1L);
        mockAuthUser = new CustomUserDetails(mockUser);

        // 인증 유저 스프링 컨텍스트 홀더에 저장
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void 쿠폰_발급_성공() throws Exception {
        // given
        CouponResponseDto couponResponseDto = new CouponResponseDto("Coupon-1234567", LocalDateTime.of(2023, 1, 1, 10, 0));
        when(couponService.issueCoupon(any(CustomUserDetails.class)))
                .thenReturn(couponResponseDto);
        // when, then
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/coupons/issue")
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "create-coupon",
                        resource(ResourceSnippetParameters.builder()
                                .description("새로운 쿠폰을 생성합니다.")
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .summary("쿠폰 생성")
                                .tag("Coupon")
                                .responseFields(List.of(
                                        fieldWithPath("message").description("성공 시 응답 : Created , 예외 시 예외 메시지"),
                                        fieldWithPath("statusCode").description("성공 상태 코드 : 201"),
                                        fieldWithPath("data.couponCode").description("쿠폰 코드"),
                                        fieldWithPath("data.issuedAt").description("생성 시간")
                                ))
                                .responseSchema(Schema.schema("쿠폰-생성-성공-응답"))
                                .build()
                        )
                ));
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.couponCode").value("Coupon-1234567"));
    }
    @Test
    void 쿠폰_사용_성공() throws Exception {
        // given
        doNothing().when(couponService).useCoupon(any(CustomUserDetails.class), any(String.class), any(Long.class));

        // when, then
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/coupons/use")
                        .queryParam("couponCode", "Coupon-1234567")
                        .queryParam("itemId", "1")
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "use-coupon",
                        resource(ResourceSnippetParameters.builder()
                                .description("쿠폰을 사용해 아이템을 일정 간격으로 끌어올립니다.")
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .queryParameters(
                                        parameterWithName("couponCode").description("쿠폰 코드"),
                                        parameterWithName("itemId").description("쿠폰을 사용할 아이템 아이디")
                                )
                                .summary("쿠폰 사용")
                                .tag("Coupon")
                                .responseFields(List.of(
                                        fieldWithPath("message").description("성공 메시지 : Ok"),
                                        fieldWithPath("statusCode").description("성공 상태 코드 : 200")
                                ))
                                .responseSchema(Schema.schema("쿠폰_사용_성공_응답"))
                                .build()
                        )
                ));
        result.andExpect(status().isOk());
    }


}
