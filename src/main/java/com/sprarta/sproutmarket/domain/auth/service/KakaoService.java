package com.sprarta.sproutmarket.domain.auth.service;

import com.google.gson.Gson;
import com.sprarta.sproutmarket.domain.auth.dto.response.KakaoProfileResponse;
import com.sprarta.sproutmarket.domain.auth.dto.response.OAuthTokenResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Getter
@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoService {
    @Value("${kakao.api_key}")
    private String kakaoApiKey;

    @Value("${kakao.redirect_uri}")
    private String kakaoRedirectUri;

    private final RestTemplate restTemplate;
    private final Gson gson;

    //인가 코드를 받아서 accessToken 을 반환
    public OAuthTokenResponse getAccessToken(String code) {
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        // HttpHeaders 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HttpBody 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoApiKey);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // POST 요청으로 Access Token 획득
        ResponseEntity<String> response = restTemplate.exchange(
                reqUrl,
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            // 응답 Body 를 OAuthToken 객체로 변환
            return gson.fromJson(response.getBody(), OAuthTokenResponse.class);
        } else {
            throw new RuntimeException("Failed to get Kakao Access Token");
        }
    }

    //accessToken 을 받아서 UserInfo 반환
    public KakaoProfileResponse getUserInfo(String accessToken) {
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        RestTemplate rt = new RestTemplate();

        //HttpHeader 오브젝트
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //http 헤더(headers)를 가진 엔티티
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        //reqUrl 로 Http 요청 , POST 방식
        ResponseEntity<String> response =
                rt.exchange(reqUrl, HttpMethod.POST, kakaoProfileRequest, String.class);

        KakaoProfileResponse kakaoProfile = new KakaoProfileResponse(response.getBody());

        return kakaoProfile;
    }

    //accessToken 을 받아서 로그아웃 시키는 메서드
    public void kakaoLogout(String accessToken) {
        String reqUrl = "https://kapi.kakao.com/v1/user/logout";

        try{
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
            log.info("[KakaoApi.kakaoLogout] responseCode : {}",  responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode <= 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line = "";
            StringBuilder responseSb = new StringBuilder();
            while((line = br.readLine()) != null){
                responseSb.append(line);
            }
            String result = responseSb.toString();
            log.info("kakao logout - responseBody = {}", result);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
