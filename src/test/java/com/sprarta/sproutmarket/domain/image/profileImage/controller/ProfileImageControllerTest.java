//package com.sprarta.sproutmarket.domain.image.profileImage.controller;
//
//import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
//import com.epages.restdocs.apispec.ResourceSnippetParameters;
//import com.epages.restdocs.apispec.Schema;
//import com.sprarta.sproutmarket.domain.CommonMockMvcControllerTestSetUp;
//import com.sprarta.sproutmarket.domain.image.profileImage.dto.ProfileImageResponse;
//import com.sprarta.sproutmarket.domain.image.profileImage.service.ProfileImageService;
//import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
//import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
//import com.sprarta.sproutmarket.domain.user.entity.User;
//import com.sprarta.sproutmarket.domain.user.enums.UserRole;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//
//import static com.epages.restdocs.apispec.ResourceDocumentation.*;
//import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
//import static org.mockito.Mockito.*;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
//import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
//import org.springframework.restdocs.payload.JsonFieldType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ProfileImageController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class ProfileImageControllerTest extends CommonMockMvcControllerTestSetUp {
//    // mockAuthUser
//    // customUserDetailService
//    @MockBean
//    private ProfileImageService profileImageService;
//
//    private User mockUser;
//
//    @BeforeEach
//    void setup(){
//        MockitoAnnotations.openMocks(this);
//        mockUser = new User(
//            1L,
//            "testUser",
//            "test@test.com",
//            "encodedOldPassword",
//            "testNickname",
//            "010-1234-5678",
//            "서울특별시 관악구 신림동",
//            UserRole.USER
//        );
//        mockAuthUser = new CustomUserDetails(mockUser);
//    }
//
//    @Test
//    @WithMockUser
//    @DisplayName("프로필 이미지 업로드 테스트")
//    void profileImageUploadTest() throws Exception {
//        // given
//        String imageName = "https://sprout-market.s3.ap-northeast-2.amazonaws.com/4da210e1-7.jpg";
//        ImageNameRequest request = new ImageNameRequest(imageName);
//        ProfileImageResponse profileImageResponse = new ProfileImageResponse(imageName);
//
//        // mocking
//        when(profileImageService.uploadProfileImage(eq(imageName), any(CustomUserDetails.class))).thenReturn(profileImageResponse);
//
//        // when & then
//        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.post("/user/profile/image")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request))
//                .header("Authorization", "Bearer (JWT 토큰)")
//            )
//            .andExpect(status().isOk())
//            .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(imageName)) // 올바른 JSON 경로 사용
//            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok")) // 메시지 검증 추가
//            .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(200)) // 상태 코드 검증 추가
//            .andDo(MockMvcRestDocumentationWrapper.document(
//                "profile-image-upload",
//                resource(ResourceSnippetParameters.builder()
//                    .description("S3에 업로드한 이미지의 주소를 DB에 저장")
//                    .summary("")
//                    .tag("ProfileImage")
//                    .queryParameters(
//                        parameterWithName("imageName").description("S3에 저장된 이미지 public URL")
//                    )
//                    .requestFields(
//                        fieldWithPath("description").type(JsonFieldType.STRING).description("등록할 S3 이미지 public 주소")
//                    )
//                    .responseFields(
//                        fieldWithPath("message").description("응답 메시지"),
//                        fieldWithPath("statusCode").description("응답 상태 코드"),
//                        fieldWithPath("data.name").description("저장된 DB 이름")
//                    )
//                    .requestSchema(Schema.schema("프로필-이미지-등록-성공-요청"))
//                    .responseSchema(Schema.schema("프로필-이미지-등록-성공-응답"))
//                    .build()
//                )
//            ));
//    }
//}