package com.sprarta.sproutmarket.domain.category.controller;

import com.epages.restdocs.apispec.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.config.SecurityConfig;
import com.sprarta.sproutmarket.domain.category.dto.CategoryRequestDto;
import com.sprarta.sproutmarket.domain.category.dto.CategoryResponseDto;
import com.sprarta.sproutmarket.domain.category.service.CategoryService;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @MockBean
    private JpaMetamodelMappingContext jpaMappingContext;

    @MockBean
    private CustomUserDetails mockAuthUser;

    @BeforeEach
    void setUp() {
        User mockUser = new User(1L, "username", "email@example.com", "encodedOldPassword", "nickname", "010-1234-5678", "address", UserRole.ADMIN);
        mockAuthUser = new CustomUserDetails(mockUser);

        // 인증 유저 스프링 컨텍스트 홀더에 저장
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void 카테고리_생성_성공() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto("가구");
        when(categoryService.create(any(CategoryRequestDto.class))).thenReturn(new CategoryResponseDto(1L,"가구"));

        ResourceSnippetParameters params = ResourceSnippetParameters.builder()
                .description("어드민 권한을 가진 사람이 카테고리를 생성할 수 있습니다.")
                .summary("카테고리 생성")
                .tag("Admin")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Bearer (JWT 토큰)")
                )
                .requestFields(
                        fieldWithPath("categoryName").type(JsonFieldType.STRING)
                                .description("카테고리 이름")
                )
                .responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("성공 시 응답 : Created , 예외 시 예외 메시지"),
                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                .description("성공 상태코드 : 201"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                                .description("응답 본문"),
                        fieldWithPath("data.categoryId").type(JsonFieldType.NUMBER)
                                .description("카테고리 ID"),
                        fieldWithPath("data.categoryName").type(JsonFieldType.STRING)
                                .description("카테고리 이름")
                )
                .requestSchema(Schema.schema("카테고리-생성-성공-요청"))
                .responseSchema(Schema.schema("카테고리-생성-성공-응답"))
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "create-category",
                        resource(params)
                ))
                .andDo(print());

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.categoryId").value(1L))
                .andExpect(jsonPath("$.data.categoryName").value("가구"));
    }

    @Test
    void 카테고리_전체_조회_성공 () throws Exception {
        CategoryResponseDto response1 = new CategoryResponseDto(1L,"가구");
        CategoryResponseDto response2 = new CategoryResponseDto(2L, "문구");
        List<CategoryResponseDto> responseDtoList = List.of(response1, response2);

        when(categoryService.getActiveCategories()).thenReturn(responseDtoList);

        ResourceSnippetParameters params = ResourceSnippetParameters.builder()
                .description("전체 카테고리를 조회할 수 있습니다.")
                .summary("전체 카테고리 조회")
                .tag("Category")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Bearer (JWT 토큰)")
                )
                .responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("성공 시 응답 : Ok , 예외 시 예외 메시지"),
                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                .description("성공 상태코드 : 200"),
                        fieldWithPath("data[]").type(JsonFieldType.ARRAY)
                                .description("응답 본문"),
                        fieldWithPath("data[].categoryId").type(JsonFieldType.NUMBER)
                                .description("카테고리 ID"),
                        fieldWithPath("data[].categoryName").type(JsonFieldType.STRING)
                                .description("카테고리 이름")
                )
                .requestSchema(Schema.schema("카테고리-생성-성공-요청"))
                .responseSchema(Schema.schema("카테고리-생성-성공-응답"))
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.get("/categories")
                .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "category-get-all",
                        resource(params)
                ))
                .andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].categoryId").value(1L))
                .andExpect(jsonPath("$.data[0].categoryName").value("가구"))
                .andExpect(jsonPath("$.data[1].categoryId").value(2L))
                .andExpect(jsonPath("$.data[1].categoryName").value("문구"));
    }

    @Test
    void 카테고리_수정_성공() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto("가구");

        when(categoryService.update(anyLong() ,any(CategoryRequestDto.class))).thenReturn(new CategoryResponseDto(1L,"가구"));

        ResourceSnippetParameters params = ResourceSnippetParameters.builder()
                .description("어드민 권한을 가진 사람이 카테고리를 수정할 수 있습니다.")
                .summary("카테고리 수정")
                .tag("Admin")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Bearer (JWT 토큰)")
                )
                .pathParameters(
                        parameterWithName("categoryId")
                                .type(SimpleType.NUMBER)
                                .description("수정할 카테고리 ID")
                )
                .requestFields(
                        fieldWithPath("categoryName").type(JsonFieldType.STRING)
                                .description("수정된 카테고리 이름")
                )
                .responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("성공 시 응답 : Ok , 예외 시 예외 메시지"),
                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                .description("성공 상태코드 : 200"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                                .description("응답 본문"),
                        fieldWithPath("data.categoryId").type(JsonFieldType.NUMBER)
                                .description("수정된 카테고리 ID"),
                        fieldWithPath("data.categoryName").type(JsonFieldType.STRING)
                                .description("수정된 카테고리 이름")
                )
                .requestSchema(Schema.schema("카테고리-수정-성공-요청"))
                .responseSchema(Schema.schema("카테고리-수정-성공-응답"))
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.patch("/admin/categories/{categoryId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "update-category",
                        resource(params)
                ))
                .andDo(print());

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryId").value(1L))
                .andExpect(jsonPath("$.data.categoryName").value("가구"));
    }

    @Test
    void 카테고리_삭제_성공() throws Exception {
        doNothing().when(categoryService).delete(anyLong());

        ResourceSnippetParameters params = ResourceSnippetParameters.builder()
                .description("어드민 권한을 가진 사람이 카테고리를 삭제할 수 있습니다.")
                .summary("카테고리 삭제")
                .tag("Admin")
                .requestHeaders(
                        headerWithName("Authorization")
                                .description("Bearer (JWT 토큰)")
                )
                .pathParameters(
                        parameterWithName("categoryId")
                                .type(SimpleType.NUMBER)
                                .description("삭제할 카테고리 ID")
                )
                .responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("성공 시 응답 : Ok , 예외 시 예외 메시지"),
                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                .description("성공 상태코드 : 200"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                                .description("성공 시 data : NULL")
                )
                .requestSchema(Schema.schema("카테고리-삭제-성공-요청"))
                .responseSchema(Schema.schema("카테고리-삭제-성공-응답"))
                .build();

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/categories/{categoryId}",3L)
                        .header("Authorization", "Bearer (JWT 토큰)"))
                .andDo(MockMvcRestDocumentationWrapper.document(
                        "delete-category",
                        resource(params)
                ))
                .andDo(print());

        result.andExpect(status().isOk());
    }
}