package com.sprarta.sproutmarket.domain.auth.controller;

import com.sprarta.sproutmarket.domain.auth.dto.response.KakaoProfileResponse;
import com.sprarta.sproutmarket.domain.auth.dto.response.OAuthTokenResponse;
import com.sprarta.sproutmarket.domain.auth.service.KakaoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@Slf4j
public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/login")
    public String loginForm(Model model){
        model.addAttribute("kakaoApiKey", kakaoService.getKakaoApiKey());
        model.addAttribute("redirectUri", kakaoService.getKakaoRedirectUri());
        return "login";
    }

    @RequestMapping("/login/oauth2/code/kakao")
    public String kakaoLogin(@RequestParam String code, HttpSession session){
        // 1. 토큰 받기
        OAuthTokenResponse oAuthToken = kakaoService.getAccessToken(code);
        String accessToken = oAuthToken.getAccess_token();

        // 2. 사용자 정보 받기
        KakaoProfileResponse kakaoProfile = kakaoService.getUserInfo(accessToken);

        // 3. 세션에 카카오 정보 저장
        session.setAttribute("email", kakaoProfile.getEmail());
        session.setAttribute("nickname", kakaoProfile.getNickname());
        session.setAttribute("profileImageUrl", kakaoProfile.getProfileImage());

        log.info("email = {}", kakaoProfile.getEmail());
        log.info("nickname = {}", kakaoProfile.getNickname());
        log.info("profileImage = {}", kakaoProfile.getProfileImage());
        log.info("accessToken = {}", accessToken);

        return "redirect:/additional-info";
    }
}
