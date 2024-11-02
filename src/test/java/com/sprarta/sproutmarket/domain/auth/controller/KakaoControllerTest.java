package com.sprarta.sproutmarket.domain.auth.controller;

import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.auth.dto.response.KakaoProfileResponse;
import com.sprarta.sproutmarket.domain.auth.dto.response.OAuthTokenResponse;
import com.sprarta.sproutmarket.domain.auth.service.KakaoService;
import com.sprarta.sproutmarket.domain.user.service.CustomUserDetailService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class KakaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KakaoService kakaoService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void loginForm_Success() throws Exception {
        String kakaoApiKey = "dummyApiKey";
        String redirectUri = "dummyRedirectUri";

        when(kakaoService.getKakaoApiKey()).thenReturn(kakaoApiKey);
        when(kakaoService.getKakaoRedirectUri()).thenReturn(redirectUri);

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("kakaoApiKey", kakaoApiKey))
                .andExpect(model().attribute("redirectUri", redirectUri));
    }

    @Test
    void kakaoLogin_Success() throws Exception {
        String code = "authCode";
        String accessToken = "accessToken";
        OAuthTokenResponse oAuthTokenResponse = new OAuthTokenResponse(accessToken, "refreshToken", "Bearer", "3600");
        KakaoProfileResponse kakaoProfileResponse = new KakaoProfileResponse("{\"id\":123,\"connected_at\":\"2024-10-31T20:44:08Z\",\"kakao_account\":{\"email\":\"email@example.com\"},\"properties\":{\"nickname\":\"nickname\",\"profile_image\":\"profile.jpg\"}}");

        when(kakaoService.getAccessToken(code)).thenReturn(oAuthTokenResponse);
        when(kakaoService.getUserInfo(accessToken)).thenReturn(kakaoProfileResponse);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/login/oauth2/code/kakao")
                        .param("code", code)
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/additional-info"));

        // 세션에 예상된 값이 설정되었는지 확인
        assertEquals("email@example.com", session.getAttribute("email"));
        assertEquals("nickname", session.getAttribute("nickname"));
        assertEquals("profile.jpg", session.getAttribute("profileImageUrl"));
    }
}

