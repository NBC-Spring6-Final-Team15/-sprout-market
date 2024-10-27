package com.sprarta.sproutmarket.domain.auth.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.auth.dto.request.SigninRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.SignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.response.SigninResponse;
import com.sprarta.sproutmarket.domain.auth.dto.response.SignupResponse;
import com.sprarta.sproutmarket.domain.auth.service.AuthService;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.service.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void signupSuccess() throws Exception {
        SignupRequest signupRequest = new SignupRequest(
                "username",
                "email@example.com",
                "password",
                "nickname",
                "010-1234-5678", // phoneNumber로 변경된 필드
                126.976889,
                37.575651,
                "USER" // userRole로 변경된 필드
        );
        SignupResponse signupResponse = new SignupResponse("jwt-token");

        when(authService.signup(any(SignupRequest.class))).thenReturn(signupResponse);
        when(jwtUtil.createToken(any(Long.class), anyString(), any(UserRole.class))).thenReturn("jwt-token");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andDo(document("auth-signup-success",
                        resource(ResourceSnippetParameters.builder()
                                .description("사용자 회원가입 API")
                                .summary("새로운 사용자를 등록합니다.")
                                .tag("Auth")
                                .requestFields(
                                        fieldWithPath("username").description("사용자 이름"),
                                        fieldWithPath("email").description("사용자 이메일"),
                                        fieldWithPath("password").description("사용자 비밀번호"),
                                        fieldWithPath("nickname").description("사용자 닉네임"),
                                        fieldWithPath("phoneNumber").description("사용자 전화번호"),
                                        fieldWithPath("longitude").description("사용자 경도"),
                                        fieldWithPath("latitude").description("사용자 위도"),
                                        fieldWithPath("userRole").description("사용자 역할")
                                )
                                .responseFields(
                                        fieldWithPath("bearerToken").description("JWT 토큰")
                                )
                                .requestSchema(Schema.schema("회원가입-성공-요청"))
                                .responseSchema(Schema.schema("회원가입-성공-응답"))
                                .build())
                ));
    }

    @Test
    void signinSuccess() throws Exception {
        SigninRequest signinRequest = new SigninRequest("email@example.com", "password");
        SigninResponse signinResponse = new SigninResponse("jwt-token");

        User user = new User(
                "username",
                "email@example.com",
                "encodedPassword",
                "nickname",
                "010-1234-5678",
                "서울특별시 종로구",
                UserRole.USER
        );

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        when(customUserDetailService.loadUserByUsername(anyString())).thenReturn(customUserDetails);
        when(authService.signin(any(SigninRequest.class))).thenReturn(signinResponse);
        when(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).thenReturn("jwt-token");

        mockMvc.perform(RestDocumentationRequestBuilders.post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signinRequest)))
                .andExpect(status().isOk())
                .andDo(document("auth-signin-success",
                        resource(ResourceSnippetParameters.builder()
                                .description("사용자 로그인 API")
                                .summary("기존 사용자로 로그인합니다.")
                                .tag("Auth")
                                .requestFields(
                                        fieldWithPath("email").description("사용자 이메일"),
                                        fieldWithPath("password").description("사용자 비밀번호")
                                )
                                .responseFields(
                                        fieldWithPath("bearerToken").description("JWT 토큰")
                                )
                                .requestSchema(Schema.schema("로그인-성공-요청"))
                                .responseSchema(Schema.schema("로그인-성공-응답"))
                                .build())
                ));
    }
}
