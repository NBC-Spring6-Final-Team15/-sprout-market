package com.sprarta.sproutmarket.domain.report.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.config.SecurityConfig;
import com.sprarta.sproutmarket.domain.report.dto.ReportRequestDto;
import com.sprarta.sproutmarket.domain.report.dto.ReportResponseDto;
import com.sprarta.sproutmarket.domain.report.enums.ReportStatus;
import com.sprarta.sproutmarket.domain.report.service.ReportService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@Import(SecurityConfig.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private ReportService reportService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @MockBean
    JpaMetamodelMappingContext jpaMappingContext;

    @MockBean
    private CustomUserDetails mockAuthUser;

    @BeforeEach
    void setUp() {
        User mockUser = new User(1L, "username", "email@example.com", "encodedOldPassword", "nickname", "010-1234-5678", "address", UserRole.USER);
        CustomUserDetails mockAuthUser = new CustomUserDetails(mockUser);

        // 인증 유저 스프링 컨텍스트 홀더에 저장
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser
    void createReport() throws Exception {
        //given
        Long itemId = 1L;
        String reportingReason = "지우개를 5만원에 팔아요";
        ReportRequestDto requestDto = new ReportRequestDto(reportingReason);
        ReportResponseDto responseDto = new ReportResponseDto(1L, 1L, reportingReason, ReportStatus.WAITING);
        when(reportService.createReport(anyLong(), any(ReportRequestDto.class), any(CustomUserDetails.class)))
                .thenReturn(responseDto);

        //when
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/reports/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document("create-report",
                        resource(ResourceSnippetParameters.builder()
                                .description("특정 매물 ID에 대한 신고를 생성합니다.")
                                .summary("매물 신고 생성")
                                .tag("report")
                                .pathParameters(
                                        parameterWithName("itemId")
                                                .description("신고할 매물 ID")
                                )
                                .requestHeaders(
                                        headerWithName("Authorization")
                                                .description("Bearer (JWT 토큰)")
                                )
                                .requestFields(
                                        fieldWithPath("reportingReason")
                                                .description("신고 사유")
                                )
                                .responseFields(
                                        fieldWithPath("message")
                                                .description("성공 메시지 : Ok"),
                                        fieldWithPath("statusCode")
                                                .description("성공 상태 코드 : 200"),
                                        fieldWithPath("data")
                                                .description("본문 응답"),
                                        fieldWithPath("data.id")
                                                .description("신고 ID"),
                                        fieldWithPath("data.itemId")
                                                .description("신고된 매물 ID"),
                                        fieldWithPath("data.reportingReason")
                                                .description("신고된 사유"),
                                        fieldWithPath("data.reportStatus")
                                                .description("신고 상태 : 기본값 WAITING")
                                )
                                .requestSchema(Schema.schema("신고-생성-성공-요청"))
                                .responseSchema(Schema.schema("신고-생성-성공-응답"))
                                .build()
                        )
                )
                );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.reportingReason").value(reportingReason));
    }

    @Test
    void getReport() {
    }

    @Test
    void getReports() {
    }

    @Test
    void updateReport() {
    }

    @Test
    void deleteReport() {
    }
}