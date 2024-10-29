package com.sprarta.sproutmarket.domain.interestedCategory.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.interestedCategory.service.InterestedCategoryService;
import com.sprarta.sproutmarket.domain.user.dto.request.UserChangePasswordRequest;
import com.sprarta.sproutmarket.domain.user.dto.request.UserDeleteRequest;
import com.sprarta.sproutmarket.domain.user.dto.response.UserResponse;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.service.CustomUserDetailService;
import com.sprarta.sproutmarket.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterestedCategoryController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
public class InterestedCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterestedCategoryService interestedCategoryService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetails mockAuthUser;

    @BeforeEach
    void setUp() {
        User mockUser = new User(1L, "username", "email@example.com", "encodedOldPassword", "nickname", "010-1234-5678", "address", UserRole.USER);
        CustomUserDetails mockAuthUser = new CustomUserDetails(mockUser);

        // Set the authenticated user in the SecurityContext
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockAuthUser, null, mockAuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userService.getUser(anyLong())).thenReturn(new UserResponse(mockUser.getId(), mockUser.getEmail()));
        doNothing().when(userService).changePassword(any(CustomUserDetails.class), any(UserChangePasswordRequest.class));
        doNothing().when(userService).deleteUser(any(CustomUserDetails.class), any(UserDeleteRequest.class));
    }

    @Test
    @WithMockUser
    void 관심카테고리_추가_성공() throws Exception {
        // when, then
        mockMvc.perform(post("/categories/{categoryId}/interest", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("add-interested-category",
                        resource(ResourceSnippetParameters.builder()
                                .description("관심 카테고리 추가 API")
                                .summary("사용자가 특정 카테고리를 관심 리스트에 추가합니다.")
                                .tag("Interested Category")
                                .pathParameters(
                                        parameterWithName("categoryId").description("관심 카테고리로 등록할 Category의 ID")
                                )
                                .responseFields(List.of(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("응답 상태 코드"),
                                        fieldWithPath("data").description("응답 데이터").optional()
                                ))
                                .responseSchema(Schema.schema("관심-카테고리-추가-성공-응답"))
                                .build())
                ));
    }

    @Test
    @WithMockUser
    void 관심카테고리_삭제_성공() throws Exception {
        // when, then
        mockMvc.perform(delete("/categories/{categoryId}/interest", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("remove-interested-category",
                        resource(ResourceSnippetParameters.builder()
                                .description("관심 카테고리 삭제 API")
                                .summary("사용자가 특정 카테고리를 관심 리스트에서 삭제합니다.")
                                .tag("Interested Category")
                                .pathParameters(
                                        parameterWithName("categoryId").description("관심 카테고리에서 삭제할 Category의 ID")
                                )
                                .responseFields(List.of(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("응답 상태 코드"),
                                        fieldWithPath("data").description("응답 데이터").optional()
                                ))
                                .responseSchema(Schema.schema("관심-카테고리-삭제-성공-응답"))
                                .build())
                ));
    }

    @Test
    @WithMockUser
    void 관심카테고리_사용자조회_성공() throws Exception {
        // when, then
        mockMvc.perform(get("/categories/{categoryId}/interested-users", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-interested-category-users",
                        resource(ResourceSnippetParameters.builder()
                                .description("특정 카테고리에 관심 있는 사용자 목록 조회 API")
                                .summary("특정 카테고리에 관심 있는 사용자 목록을 조회합니다.")
                                .tag("Interested Category")
                                .pathParameters(
                                        parameterWithName("categoryId").description("관심이 있는 사용자 목록을 조회할 Category의 ID")
                                )
                                .responseFields(List.of(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("응답 상태 코드"),
                                        fieldWithPath("data").description("관심 있는 사용자 목록")
                                ))
                                .responseSchema(Schema.schema("관심-카테고리-사용자-조회-성공-응답"))
                                .build())
                ));
    }
}
