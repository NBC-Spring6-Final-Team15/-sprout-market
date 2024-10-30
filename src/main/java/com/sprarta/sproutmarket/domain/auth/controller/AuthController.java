package com.sprarta.sproutmarket.domain.auth.controller;

import com.sprarta.sproutmarket.domain.auth.dto.request.AdminSignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.SigninRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.SignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.response.SigninResponse;
import com.sprarta.sproutmarket.domain.auth.dto.response.SignupResponse;
import com.sprarta.sproutmarket.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/auth/signin") // 로그인
    public SigninResponse signin(@Valid @RequestBody SigninRequest request) {
        return authService.signin(request);
    }

    @PostMapping("/adminUser/signup") // 관리자 회원가입
    public SignupResponse adminSignup(@Valid @RequestBody AdminSignupRequest request) {
        return authService.adminSignup(request);
    }

    @PostMapping("/adminUser/signin") // 관리자 로그인
    public SigninResponse adminSignin(@Valid @RequestBody SigninRequest request) {
        return authService.adminSignin(request);
    }
}
