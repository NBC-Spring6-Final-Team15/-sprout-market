package com.sprarta.sproutmarket.domain.kakao;

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
    public String kakaoLogin(@RequestParam String code){
        // 1. 인가 코드 받기 (@RequestParam String code)

        // 2. 토큰 받기
        OAuthToken oAuthToken = kakaoApi.getAccessToken(code);
        String accessToken = oAuthToken.getAccess_token();

        // 3. 사용자 정보 받기
        KakaoProfile kakaoProfile = kakaoApi.getUserInfo(accessToken);

        // 사용자 정보 출력
        String email = kakaoProfile.getEmail();
        String nickname = kakaoProfile.getNickname();
        String profileImage = kakaoProfile.getProfileImage();

        System.out.println("email = " + email);
        System.out.println("nickname = " + nickname);
        System.out.println("profileImage = " + profileImage);
        System.out.println("accessToken = " + accessToken);

        return "redirect:/result";
    }
}
