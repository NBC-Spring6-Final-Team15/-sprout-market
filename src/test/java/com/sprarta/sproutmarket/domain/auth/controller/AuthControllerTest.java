package com.sprarta.sproutmarket.domain.auth.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.auth.dto.request.AdminSignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.SigninRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.SignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.response.SigninResponse;
import com.sprarta.sproutmarket.domain.auth.dto.response.SignupResponse;
import com.sprarta.sproutmarket.domain.auth.service.AuthService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
    void adminSignupSuccess() throws Exception {
        AdminSignupRequest adminSignupRequest = new AdminSignupRequest(
                "adminUsername",
                "admin@example.com",
                "adminPassword",
                "adminNickname",
                "010-1234-5678"
        );
        SignupResponse signupResponse = new SignupResponse("jwt-token");

        when(authService.adminSignup(any(AdminSignupRequest.class))).thenReturn(signupResponse);

        mockMvc.perform(post("/adminUser/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminSignupRequest)))
                .andExpect(status().isOk())
                .andDo(document("auth-admin-signup-success",
                        resource(ResourceSnippetParameters.builder()
                                .description("관리자 회원가입 API")
                                .summary("새로운 관리자를 등록합니다.")
                                .tag("Auth")
                                .requestFields(
                                        fieldWithPath("username").description("관리자 이름"),
                                        fieldWithPath("email").description("관리자 이메일"),
                                        fieldWithPath("password").description("관리자 비밀번호"),
                                        fieldWithPath("nickname").description("관리자 닉네임"),
                                        fieldWithPath("phoneNumber").description("관리자 전화번호")
                                )
                                .responseFields(
                                        fieldWithPath("bearerToken").description("JWT 토큰")
                                )
                                .requestSchema(Schema.schema("관리자-회원가입-성공-요청"))
                                .responseSchema(Schema.schema("관리자-회원가입-성공-응답"))
                                .build())
                ));
    }

    @Test
    void adminSigninSuccess() throws Exception {
        SigninRequest adminSigninRequest = new SigninRequest("admin@example.com", "adminPassword");
        SigninResponse signinResponse = new SigninResponse("jwt-token");

        when(authService.adminSignin(any(SigninRequest.class))).thenReturn(signinResponse);

        mockMvc.perform(post("/adminUser/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminSigninRequest)))
                .andExpect(status().isOk())
                .andDo(document("auth-admin-signin-success",
                        resource(ResourceSnippetParameters.builder()
                                .description("관리자 로그인 API")
                                .summary("기존 관리자 계정으로 로그인합니다.")
                                .tag("Auth")
                                .requestFields(
                                        fieldWithPath("email").description("관리자 이메일"),
                                        fieldWithPath("password").description("관리자 비밀번호")
                                )
                                .responseFields(
                                        fieldWithPath("bearerToken").description("JWT 토큰")
                                )
                                .requestSchema(Schema.schema("관리자-로그인-성공-요청"))
                                .responseSchema(Schema.schema("관리자-로그인-성공-응답"))
                                .build())
                ));
    }

    @Test
    void userSignupSuccess() throws Exception {
        SignupRequest signupRequest = new SignupRequest(
                "username",
                "user@example.com",
                "userPassword",
                "userNickname",
                "010-1234-5678",
                126.976889,
                37.575651
        );
        SignupResponse signupResponse = new SignupResponse("jwt-token");

        when(authService.signup(any(SignupRequest.class))).thenReturn(signupResponse);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andDo(document("auth-user-signup-success",
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
                                        fieldWithPath("latitude").description("사용자 위도")
                                )
                                .responseFields(
                                        fieldWithPath("bearerToken").description("JWT 토큰")
                                )
                                .requestSchema(Schema.schema("사용자-회원가입-성공-요청"))
                                .responseSchema(Schema.schema("사용자-회원가입-성공-응답"))
                                .build())
                ));
    }

    @Test
    void userSigninSuccess() throws Exception {
        SigninRequest signinRequest = new SigninRequest("user@example.com", "userPassword");
        SigninResponse signinResponse = new SigninResponse("jwt-token");

        when(authService.signin(any(SigninRequest.class))).thenReturn(signinResponse);

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signinRequest)))
                .andExpect(status().isOk())
                .andDo(document("auth-user-signin-success",
                        resource(ResourceSnippetParameters.builder()
                                .description("사용자 로그인 API")
                                .summary("기존 사용자 계정으로 로그인합니다.")
                                .tag("Auth")
                                .requestFields(
                                        fieldWithPath("email").description("사용자 이메일"),
                                        fieldWithPath("password").description("사용자 비밀번호")
                                )
                                .responseFields(
                                        fieldWithPath("bearerToken").description("JWT 토큰")
                                )
                                .requestSchema(Schema.schema("사용자-로그인-성공-요청"))
                                .responseSchema(Schema.schema("사용자-로그인-성공-응답"))
                                .build())
                ));
    }
}
