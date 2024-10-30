package com.sprarta.sproutmarket.domain.review.controller;


import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.config.SecurityConfig;
import com.sprarta.sproutmarket.domain.review.dto.ReviewRequestDto;
import com.sprarta.sproutmarket.domain.review.dto.ReviewResponseDto;
import com.sprarta.sproutmarket.domain.review.enums.ReviewRating;
import com.sprarta.sproutmarket.domain.review.service.ReviewService;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.service.CustomUserDetailService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import(SecurityConfig.class)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ReviewService reviewService;

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
    void 리뷰_생성_성공() throws Exception {
        // given
        Long tradeId = 1L;
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto("좋아요", ReviewRating.GOOD);
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(1L, tradeId, "좋아요", ReviewRating.GOOD);

        when(reviewService.createReview(any(Long.class), any(ReviewRequestDto.class), any(CustomUserDetails.class)))
                .thenReturn(reviewResponseDto);

        // when, then
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/reviews/trades/{tradeId}", tradeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDto))
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "create-review",
                        resource(ResourceSnippetParameters.builder()
                                .description("새로운 리뷰를 생성합니다.")
                                .pathParameters(
                                        parameterWithName("tradeId").description("거래 ID")
                                )
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .summary("리뷰 생성")
                                .tag("Review")
                                .requestFields(List.of(
                                        fieldWithPath("comment").description("리뷰 내용"),
                                        fieldWithPath("reviewRating").description("리뷰 평점")
                                ))
                                .responseFields(List.of(
                                        fieldWithPath("message").description("성공 시 응답 : Created , 예외 시 예외 메시지"),
                                        fieldWithPath("statusCode").description("성공 상태 코드 : 201"),
                                        fieldWithPath("data.id").description("리뷰 ID"),
                                        fieldWithPath("data.tradeId").description("거래 ID"),
                                        fieldWithPath("data.comment").description("리뷰 내용"),
                                        fieldWithPath("data.reviewRating").description("리뷰 평점")
                                ))
                                .requestSchema(Schema.schema("리뷰-생성-성공-요청"))
                                .responseSchema(Schema.schema("리뷰-생성-성공-응답"))
                                .build()
                        )
                ));
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.comment").value(reviewRequestDto.getComment()))
                .andExpect(jsonPath("$.data.reviewRating").value("GOOD"));
    }

    @Test
    void 리뷰_조회_성공() throws Exception {
        // given
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(1L, 2L, "좋아요", ReviewRating.GOOD);

        when(reviewService.getReview(anyLong())).thenReturn(reviewResponseDto);

        // when, then
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/reviews/{reviewId}", 1L)
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "get-review",
                        resource(ResourceSnippetParameters.builder()
                                .description("특정 리뷰의 정보를 조회합니다.")
                                .pathParameters(
                                        parameterWithName("reviewId").description("조회할 리뷰 ID")
                                )
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .summary("리뷰 단건 조회")
                                .tag("Review")
                                .responseFields(List.of(
                                        fieldWithPath("message").description("성공 메시지 : Ok"),
                                        fieldWithPath("statusCode").description("성공 상태 코드 : 200"),
                                        fieldWithPath("data.id").description("리뷰 ID"),
                                        fieldWithPath("data.tradeId").description("관련 거래 ID"),
                                        fieldWithPath("data.comment").description("리뷰 내용"),
                                        fieldWithPath("data.reviewRating").description("리뷰 평점")
                                ))
                                .responseSchema(Schema.schema("리뷰-조회-성공-응답"))
                                .build()
                        )
                ));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comment").value(reviewResponseDto.getComment()))
                .andExpect(jsonPath("$.data.reviewRating").value("GOOD"));
    }

    @Test
    void 리뷰_전체조회_성공() throws Exception {
        // given
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(1L, 2L, "좋은 거래", ReviewRating.GOOD);
        ReviewResponseDto reviewResponseDto2 = new ReviewResponseDto(2L, 3L, "좋았습니다", ReviewRating.GOOD);

        List<ReviewResponseDto> responseDtoList = new ArrayList<>();
        responseDtoList.add(reviewResponseDto);
        responseDtoList.add(reviewResponseDto2);

        when(reviewService.getReviews(anyLong())).thenReturn(responseDtoList);

        // when, then
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/reviews/users/{userId}", 1L)
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "get-reviews",
                        resource(ResourceSnippetParameters.builder()
                                .description("특정 사용자의 리뷰 정보를 조회합니다.")
                                .pathParameters(
                                        parameterWithName("userId").description("조회할 사용자의 ID")
                                )
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .summary("리뷰 전체 조회")
                                .tag("Review")
                                .responseFields(List.of(
                                        fieldWithPath("message").description("성공 메시지 : Ok"),
                                        fieldWithPath("statusCode").description("성공 상태 코드 : 200"),
                                        fieldWithPath("data[]").description("리뷰 목록"),
                                        fieldWithPath("data[].id").description("리뷰 ID"),
                                        fieldWithPath("data[].tradeId").description("관련 거래 ID"),
                                        fieldWithPath("data[].comment").description("리뷰 내용"),
                                        fieldWithPath("data[].reviewRating").description("리뷰 평점")
                                ))
                                .responseSchema(Schema.schema("리뷰-전체조회-성공-응답"))
                                .build()
                        )
                ));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.hasSize(2)));
    }

    @Test
    void 리뷰_수정_성공() throws Exception {
        // given
        Long tradeId = 1L;
        ReviewRequestDto reviewRequestDto = new ReviewRequestDto("좋아요", ReviewRating.GOOD);
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto(1L, tradeId, "좋아요", ReviewRating.GOOD);

        when(reviewService.updateReview(any(Long.class), any(ReviewRequestDto.class), any(CustomUserDetails.class)))
                .thenReturn(reviewResponseDto);

        // when, then
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.put("/reviews/{reviewId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequestDto))
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "update-review",
                        resource(ResourceSnippetParameters.builder()
                                .description("특정 리뷰의 정보를 수정합니다.")
                                .pathParameters(
                                        parameterWithName("reviewId").description("수정할 리뷰 ID")
                                )
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .summary("리뷰 수정")
                                .tag("Review")
                                .requestFields(List.of(
                                        fieldWithPath("comment").description("리뷰 코멘트"),
                                        fieldWithPath("reviewRating").description("리뷰 평점")
                                ))
                                .responseFields(List.of(
                                        fieldWithPath("message").description("성공 메시지 : Ok"),
                                        fieldWithPath("statusCode").description("성공 상태 코드 : 200"),
                                        fieldWithPath("data.id").description("리뷰 ID"),
                                        fieldWithPath("data.tradeId").description("관련 거래 ID"),
                                        fieldWithPath("data.comment").description("리뷰 내용"),
                                        fieldWithPath("data.reviewRating").description("리뷰 평점")
                                ))
                                .requestSchema(Schema.schema("리뷰-수정-성공-요청"))
                                .responseSchema(Schema.schema("리뷰-수정-성공-응답"))
                                .build()
                        )
                ));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comment").value(reviewResponseDto.getComment()))
                .andExpect(jsonPath("$.data.reviewRating").value("GOOD"));
    }

    @Test
    void 리뷰_삭제_성공() throws Exception {
        // given

        doNothing().when(reviewService).deleteReview(anyLong(), any(CustomUserDetails.class));

        // when, then
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/reviews/{reviewId}", 1L)
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(print())
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "delete-review",
                        resource(ResourceSnippetParameters.builder()
                                .description("특정 리뷰를 삭제합니다.")
                                .pathParameters(
                                        parameterWithName("reviewId").description("삭제할 리뷰 ID")
                                )
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .summary("리뷰 삭제")
                                .tag("Review")
                                .responseFields(List.of(
                                        fieldWithPath("message").description("성공 메시지 : Ok"),
                                        fieldWithPath("statusCode").description("성공 상태 코드 : 200")
                                ))
                                .responseSchema(Schema.schema("리뷰-삭제-성공-응답"))
                                .build()
                        )
                ));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }


}
