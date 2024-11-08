package com.sprarta.sproutmarket.domain.image.profileImage.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.sprarta.sproutmarket.domain.CommonMockMvcControllerTestSetUp;
import com.sprarta.sproutmarket.domain.image.dto.response.ImageResponse;
import com.sprarta.sproutmarket.domain.image.profileImage.service.ProfileImageService;
import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileImageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileImageControllerTest extends CommonMockMvcControllerTestSetUp {
    @MockBean
    private ProfileImageService profileImageService;
    @InjectMocks
    private ProfileImageController profileImageController;
    private User mockUser;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        mockUser = new User(
            "testUser",
            "test@test.com",
            "encodedOldPassword",
            "testNickname",
            "010-1234-5678",
            "서울특별시 관악구 신림동",
            UserRole.USER
        );

        // 실제 User 객체로부터 CustomUserDetails 인스턴스를 생성
        mockAuthUser = new CustomUserDetails(mockUser);  // mockAuthUser를 실제로 인스턴스화합니다.

        // SecurityContext에 인증 설정
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(mockAuthUser, null)
        );
    }

    @Test
    @WithMockUser
    @DisplayName("프로필 이미지 등록 성공")
    void profileImage_upload_success() throws Exception {
        ImageResponse mockResponse = new ImageResponse("image.jpg");
        given(profileImageService.uploadProfileImage(any(ImageNameRequest.class), any(CustomUserDetails.class)))
            .willReturn(mockResponse);

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/user/profile/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"imageName\":\"image.jpg\"}")
                .header("Authorization", "Bearer (JWT 토큰)")
            )
            .andDo(MockMvcRestDocumentationWrapper.document(
                "profile-image-upload",
                resource(ResourceSnippetParameters.builder()
                    .description("프로필 이미지 등록")
                    .summary("프로필 이미지 등록 API")
                    .tag("profile-image")
                    .requestHeaders(
                        headerWithName("Authorization").description("Bearer (JWT 토큰)")
                    )
                    .requestFields(
                        fieldWithPath("imageName").type(JsonFieldType.STRING)
                            .description("프로필 이미지의 public 주소")
                    )
                    .responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("성공 상태 메시지"),
                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                            .description("성공 시 응답 코드 : 200"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("응답 본문").optional(),
                        fieldWithPath("data.name").type(JsonFieldType.STRING)
                            .description("저장된 이미지 이름")
                    )
                    .requestSchema(Schema.schema("프로필-이미지-등록-성공-요청"))
                    .responseSchema(Schema.schema("프로필-이미지-등록-성공-응답"))
                    .build()
                )
            ));
        verify(profileImageService,times(1)).uploadProfileImage(any(ImageNameRequest.class),any(CustomUserDetails.class));
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("image.jpg"));
    }

    @Test
    @WithMockUser
    @DisplayName("프로필 이미지 삭제 성공")
    void profileImage_delete_success() throws Exception {
        doNothing().when(profileImageService).deleteProfileImage(any(ImageNameRequest.class), any(CustomUserDetails.class));

        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/user/profile/image")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"imageName\":\"image.jpg\"}")
                .header("Authorization", "Bearer (JWT 토큰)")
            )
            .andDo(MockMvcRestDocumentationWrapper.document(
                "profile-image-delete",
                resource(ResourceSnippetParameters.builder()
                    .description("프로필 이미지 삭제")
                    .summary("프로필 이미지 삭제 API")
                    .tag("profile-image")
                    .requestHeaders(
                        headerWithName("Authorization").description("Bearer (JWT 토큰)")
                    )
                    .requestFields(
                        fieldWithPath("imageName").type(JsonFieldType.STRING)
                            .description("프로필 이미지의 public 주소")
                    )
                    .responseFields(
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("성공 상태 메시지"),
                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                            .description("성공 시 응답 코드 : 200")
                    )
                    .requestSchema(Schema.schema("프로필-이미지-삭제-성공-요청"))
                    .responseSchema(Schema.schema("프로필-이미지-삭제-성공-응답"))
                    .build()
                )
            ));
        verify(profileImageService,times(1)).deleteProfileImage(any(ImageNameRequest.class),any(CustomUserDetails.class));
        result.andExpect(status().isOk());
    }
}