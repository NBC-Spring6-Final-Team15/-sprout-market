package com.sprarta.sproutmarket.domain.auth.controller;

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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
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
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signupSuccess() throws Exception {
        // Given
        SignupRequest signupRequest = new SignupRequest(
                "username",
                "email@example.com",
                "password",
                "nickname",
                "010-1234-5678",
                126.976889,
                37.575651,
                "USER"
        );
        SignupResponse signupResponse = new SignupResponse("jwt-token");

        // 모의 객체가 JWT 토큰 생성
        when(authService.signup(any(SignupRequest.class))).thenReturn(signupResponse);
        when(jwtUtil.createToken(any(Long.class), anyString(), any(UserRole.class))).thenReturn("jwt-token");

        // When & Then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(signupResponse)));
    }

    @Test
    void signinSuccess() throws Exception {
        // Given
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

        // 이메일로 유저 찾기와 JWT 토큰 생성 모의
        CustomUserDetails customUserDetails = new CustomUserDetails(user);  // CustomUserDetails가 UserDetails 구현
        when(customUserDetailService.loadUserByUsername(anyString())).thenReturn(customUserDetails);
        when(authService.signin(any(SigninRequest.class))).thenReturn(signinResponse);
        when(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).thenReturn("jwt-token");

        // When & Then
        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signinRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(signinResponse)));
    }

    @Test
    void signupFail_EmailAlreadyExists() throws Exception {
        // Given
        SignupRequest signupRequest = new SignupRequest(
                "username",
                "email@example.com",
                "password",
                "nickname",
                "010-1234-5678",
                126.976889,
                37.575651,
                "USER"
        );

        when(authService.signup(any(SignupRequest.class))).thenThrow(new ApiException(ErrorStatus.NOT_FOUND_EMAIL));

        // When & Then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void signinFail_UserNotFound() throws Exception {
        // Given
        SigninRequest signinRequest = new SigninRequest("email@example.com", "password");

        when(authService.signin(any(SigninRequest.class))).thenThrow(new ApiException(ErrorStatus.NOT_FOUND_AUTH_USER));

        // When & Then
        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signinRequest)))
                .andExpect(status().isNotFound());
    }
}