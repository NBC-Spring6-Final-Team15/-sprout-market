package com.sprarta.sproutmarket.domain.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/chat")
    public String chat() {return "chat";}

    @GetMapping("/additional-info")
    public String additionalInfo() {return "additional-info";}
}

