package com.sprarta.sproutmarket.domain.auth.service;

import com.google.gson.Gson;
import com.sprarta.sproutmarket.domain.auth.dto.response.KakaoProfileResponse;
import com.sprarta.sproutmarket.domain.auth.dto.response.OAuthTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class KakaoServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KakaoService kakaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kakaoService = new KakaoService(restTemplate, new Gson());  // Gson 객체를 초기화하여 주입
    }

    @Test
    void getAccessToken_Success() {
        ResponseEntity<String> mockResponse = mock(ResponseEntity.class);
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
        when(mockResponse.getBody()).thenReturn("{\"access_token\":\"test_token\"}");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        OAuthTokenResponse result = kakaoService.getAccessToken("test_code");
        assertNotNull(result);
        assertEquals("test_token", result.getAccess_token());
    }
}

