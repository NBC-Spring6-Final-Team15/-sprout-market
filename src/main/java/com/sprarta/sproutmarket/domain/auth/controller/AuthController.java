package com.sprarta.sproutmarket.domain.auth.controller;

import com.sprarta.sproutmarket.domain.auth.dto.request.*;
import com.sprarta.sproutmarket.domain.auth.dto.response.SigninResponse;
import com.sprarta.sproutmarket.domain.auth.dto.response.SignupResponse;
import com.sprarta.sproutmarket.domain.auth.service.AuthService;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/auth/kakao-signup")
    public SignupResponse kakaoSignup(@Valid @RequestBody KakaoSignupRequest request, HttpSession session) {
        return authService.kakaoSignup(request, session);
    }

    // 로그인 시, user/admin 확인을 위한 코드
    @GetMapping("/auth/checkRole")
    public ResponseEntity<Map<String, String>> checkRole(@RequestParam String email) {
        UserRole role = authService.findUserRoleByEmail(email);
        Map<String, String> response = new HashMap<>();
        response.put("userRole", role.name());
        return ResponseEntity.ok(response);
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
