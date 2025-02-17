package com.sprarta.sproutmarket.domain.auth.service;

import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.auth.dto.request.*;
import com.sprarta.sproutmarket.domain.auth.dto.response.SigninResponse;
import com.sprarta.sproutmarket.domain.auth.dto.response.SignupResponse;
import com.sprarta.sproutmarket.domain.common.RedisUtil;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${sprout.market.admin.key}")
    private String adminKey;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final RedisUtil redisUtil;

    private static final String AUTH_EMAIL_KEY = "AuthEmail:";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Transactional
    public SignupResponse signup(SignupRequest request) { return createUser(request); }

    @Transactional
    public SignupResponse adminSignup(AdminSignupRequest request) { return createAdminUser(request); }

    public SigninResponse signin(SigninRequest request) {
        return authenticateUser(request, UserRole.USER);
    }

    public SigninResponse adminSignin(SigninRequest request) {
        return authenticateUser(request, UserRole.ADMIN);
    }

    @Transactional
    public SignupResponse kakaoSignup(KakaoSignupRequest request, HttpSession session) {
        // 세션에서 카카오 로그인 정보를 가져옴
        String email = (String) session.getAttribute("email");
        String nickname = (String) session.getAttribute("nickname");
        String profileImageUrl = (String) session.getAttribute("profileImageUrl");

        if (userRepository.existsByEmail(email)) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_EMAIL);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성 (forKakao 사용)
        User newUser = User.forKakao(
                request.getUsername(),
                email,
                nickname,
                encodedPassword,
                request.getPhoneNumber(),
                request.getAddress(),
                profileImageUrl,
                UserRole.USER
        );

        // DB 저장
        userRepository.save(newUser);

        // JWT 토큰 생성
        String bearerToken = jwtUtil.createToken(newUser.getId(), newUser.getEmail(), UserRole.USER);

        return new SignupResponse(bearerToken);
    }

    private SignupResponse createUser(SignupRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_EMAIL);
        }

        // 비밀번호 유효성 검사
        if (!isPasswordValid(request.getPassword())) {
            throw new ApiException(ErrorStatus.INVALID_PASSWORD_FORM);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User(
                request.getUsername(),
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getPhoneNumber(),
                request.getAddress(),
                UserRole.USER
        );

        userRepository.save(newUser);
        String bearerToken = jwtUtil.createToken(newUser.getId(), newUser.getEmail(), UserRole.USER);

        return new SignupResponse(bearerToken);
    }

    private SignupResponse createAdminUser(AdminSignupRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_EMAIL);
        }

        // 비밀번호 유효성 검사
        if (!isPasswordValid(request.getPassword())) {
            throw new ApiException(ErrorStatus.INVALID_PASSWORD_FORM);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User(
                request.getUsername(),
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getPhoneNumber(),
                null, // adminSignup 에서는 address 가 필요하지 않으므로 null로 설정
                UserRole.ADMIN
        );

        userRepository.save(newUser);
        String bearerToken = jwtUtil.createToken(newUser.getId(), newUser.getEmail(), UserRole.ADMIN);

        return new SignupResponse(bearerToken);
    }

    private SigninResponse authenticateUser(SigninRequest request, UserRole requiredRole) {
        User user = userRepository.findByEmailAndStatusIsActive(request.getEmail()).orElseThrow(
        ()-> new ApiException(ErrorStatus.NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_PASSWORD);
        }

        if (user.getUserRole() != requiredRole) {
            throw new ApiException(ErrorStatus.FORBIDDEN_ACCESS);
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());

        return new SigninResponse(bearerToken);
    }

    public void verifyEmail(EmailVerificationDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_EMAIL);
        }

        String email = requestDto.getEmail();
        String redisKey = AUTH_EMAIL_KEY + requestDto.getEmail();
        Integer authNumber = (Integer) redisUtil.get(redisKey);

        // 메일 인증 중인 email 인지 확인
        if (authNumber == null) {
            emailService.sendEmail(redisKey, email);
            throw new ApiException(ErrorStatus.SEND_AUTH_EMAIL);
        }

        // 인증번호 확인
        if (authNumber != requestDto.getAuthNumber()) {
            throw new ApiException(ErrorStatus.FAIL_EMAIL_AUTHENTICATION);
        }

        redisUtil.delete(redisKey);
    }

    public UserRole findUserRoleByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorStatus.NOT_FOUND_AUTH_USER));
        return user.getUserRole();  // User 엔티티의 역할 반환
    }

    private boolean isPasswordValid(String password) {
        return StringUtils.hasText(password) && Pattern.matches(PASSWORD_PATTERN, password);
    }
}
