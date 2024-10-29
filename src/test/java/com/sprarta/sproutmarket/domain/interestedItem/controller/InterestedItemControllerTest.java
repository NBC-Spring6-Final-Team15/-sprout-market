package com.sprarta.sproutmarket.domain.interestedItem.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.interestedItem.service.InterestedItemService;
import com.sprarta.sproutmarket.domain.item.dto.response.ItemResponseDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterestedItemController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ExtendWith(RestDocumentationExtension.class)
public class InterestedItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterestedItemService interestedItemService;

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
    void 관심상품추가성공() throws Exception {
        // when, then
        mockMvc.perform(post("/items/{itemId}/interest", 1L))
                .andExpect(status().isOk())
                .andDo(document("add-interested-item",
                        resource(ResourceSnippetParameters.builder()
                                .description("관심 상품 추가 API")
                                .summary("사용자가 특정 상품을 관심 리스트에 추가합니다.")
                                .tag("Interested Item")
                                .pathParameters(
                                        parameterWithName("itemId").description("관심 상품으로 등록할 Item의 ID")
                                )
                                .responseFields(List.of(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("응답 상태 코드"),
                                        fieldWithPath("data").description("응답 데이터").optional()
                                ))
                                .responseSchema(Schema.schema("관심-상품-추가-성공-응답"))
                                .build())
                ));
    }

    @Test
    @WithMockUser
    void 관심상품삭제성공() throws Exception {
        // when, then
        mockMvc.perform(delete("/items/{itemId}/interest", 1L))
                .andExpect(status().isOk())
                .andDo(document("remove-interested-item",
                        resource(ResourceSnippetParameters.builder()
                                .description("관심 상품 삭제 API")
                                .summary("사용자가 특정 상품을 관심 리스트에서 삭제합니다.")
                                .tag("Interested Item")
                                .pathParameters(
                                        parameterWithName("itemId").description("관심 상품에서 삭제할 Item의 ID")
                                )
                                .responseFields(List.of(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("응답 상태 코드"),
                                        fieldWithPath("data").description("응답 데이터").optional()
                                ))
                                .responseSchema(Schema.schema("관심-상품-삭제-성공-응답"))
                                .build())
                ));
    }

    @Test
    @WithMockUser
    void 관심상품조회성공() throws Exception {
        // given
        Pageable pageable = PageRequest.of(1, 10);
        Page<ItemResponseDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        given(interestedItemService.getInterestedItems(any(CustomUserDetails.class), anyInt(), anyInt()))
                .willReturn(emptyPage); // 빈 페이지를 반환하는 경우

        // when, then
        mockMvc.perform(get("/items/interested")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("get-interested-items",
                        resource(ResourceSnippetParameters.builder()
                                .description("관심 상품 조회 API")
                                .summary("현재 사용자의 관심 상품을 페이지네이션으로 조회합니다.")
                                .tag("Interested Item")
                                .queryParameters(
                                        parameterWithName("page").description("페이지 번호 (1부터 시작)"),
                                        parameterWithName("size").description("페이지당 조회할 관심 상품 개수")
                                )
                                .responseFields(List.of(
                                        fieldWithPath("message").description("응답 메시지"),
                                        fieldWithPath("statusCode").description("응답 상태 코드"),
                                        fieldWithPath("data.content[]").description("관심 상품 목록 (빈 배열일 수 있음)"),
                                        fieldWithPath("data.pageable.pageNumber").description("현재 페이지 번호"),
                                        fieldWithPath("data.pageable.pageSize").description("페이지당 항목 수"),
                                        fieldWithPath("data.pageable.offset").description("오프셋"),
                                        fieldWithPath("data.pageable.paged").description("페이지 처리 여부"),
                                        fieldWithPath("data.pageable.unpaged").description("비페이지 처리 여부"),
                                        fieldWithPath("data.pageable.sort.empty").description("정렬 조건이 비어 있는지 여부"),
                                        fieldWithPath("data.pageable.sort.unsorted").description("정렬되지 않은 상태 여부"),
                                        fieldWithPath("data.pageable.sort.sorted").description("정렬 여부"),
                                        fieldWithPath("data.last").description("마지막 페이지 여부"),
                                        fieldWithPath("data.totalPages").description("총 페이지 수"),
                                        fieldWithPath("data.totalElements").description("총 항목 수"),
                                        fieldWithPath("data.size").description("페이지당 항목 수"),
                                        fieldWithPath("data.number").description("현재 페이지 번호"),
                                        fieldWithPath("data.sort.empty").description("정렬 조건이 비어 있는지 여부"),
                                        fieldWithPath("data.sort.unsorted").description("정렬되지 않은 상태 여부"),
                                        fieldWithPath("data.sort.sorted").description("정렬 여부"),
                                        fieldWithPath("data.first").description("첫 번째 페이지 여부"),
                                        fieldWithPath("data.numberOfElements").description("현재 페이지에서 조회된 항목 수"),
                                        fieldWithPath("data.empty").description("페이지가 비어 있는지 여부")
                                ))
                                .responseSchema(Schema.schema("관심-상품-조회-성공-응답"))
                                .build())
                ));
    }
}
