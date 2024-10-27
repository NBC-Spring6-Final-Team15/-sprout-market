package com.sprarta.sproutmarket.domain.apiResponseExample;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {

    @GetMapping("/signin")
    public String signin() {
        // templates/signin.html 파일을 렌더링
        return "signin";  // "signin"은 templates 폴더 아래의 signin.html을 가리킵니다.
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";  // templates 폴더 아래의 signup.html 렌더링
    }
}

