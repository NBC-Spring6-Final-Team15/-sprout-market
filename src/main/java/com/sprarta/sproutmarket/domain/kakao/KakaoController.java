package com.sprarta.sproutmarket.domain.kakao;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class KakaoController {

    private final KakaoApi kakaoApi;

    @GetMapping("/login")
    public String loginForm(Model model){
        model.addAttribute("kakaoApiKey", kakaoApi.getKakaoApiKey());
        model.addAttribute("redirectUri", kakaoApi.getKakaoRedirectUri());
        return "login";
    }

    @RequestMapping("/login/oauth2/code/kakao")
    public String kakaoLogin(@RequestParam String code, HttpSession session){
        // 1. 토큰 받기
        OAuthToken oAuthToken = kakaoApi.getAccessToken(code);
        String accessToken = oAuthToken.getAccess_token();

        // 2. 사용자 정보 받기
        KakaoProfile kakaoProfile = kakaoApi.getUserInfo(accessToken);

        // 3. 세션에 카카오 정보 저장
        session.setAttribute("email", kakaoProfile.getEmail());
        session.setAttribute("nickname", kakaoProfile.getNickname());
        session.setAttribute("profileImageUrl", kakaoProfile.getProfileImage());

        System.out.println("email = " + kakaoProfile.getEmail());
        System.out.println("nickname = " + kakaoProfile.getNickname());
        System.out.println("profileImage = " + kakaoProfile.getProfileImage());
        System.out.println("accessToken = " + accessToken);

        return "redirect:/additional-info";
    }
}
