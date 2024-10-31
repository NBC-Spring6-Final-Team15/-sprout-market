package com.sprarta.sproutmarket.domain.auth.controller;

import com.sprarta.sproutmarket.domain.auth.dto.request.AdminSignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.EmailVerificationDto;
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

    /**
     * 이메일 인증 요청
     * 1. 이메일만 보내서 검증 후 이메일 발송
     * 2. 코드 입력하면 authNumber에 입력된 코드 채워서 맞는지 확인
     * 3. redis에서 해당 레코드 삭제
     * @param emailVerificationDto 이메일(필수), 인증 코드를 담아서 요청을 보냄
     */
    @PostMapping("/auth/email")
    public void emailVerification(@RequestBody @Valid EmailVerificationDto emailVerificationDto) {
        authService.verifyEmail(emailVerificationDto);
    }
}
