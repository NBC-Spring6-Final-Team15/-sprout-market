package com.sprarta.sproutmarket.domain.auth.service;

import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.auth.dto.request.AdminSignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.EmailVerificationDto;
import com.sprarta.sproutmarket.domain.auth.dto.request.SigninRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.SignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.response.SigninResponse;
import com.sprarta.sproutmarket.domain.auth.dto.response.SignupResponse;
import com.sprarta.sproutmarket.domain.common.RedisUtil;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final String AUTH_EMAIL_KEY = "authEmail:";

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        return createUser(request, UserRole.USER);
    }

    @Transactional
    public SignupResponse adminSignup(AdminSignupRequest request) {
        if (!request.getAdminKey().equals(adminKey)) {
            throw new ApiException(ErrorStatus.INVALID_ADMIN_KEY);
        }

        return createAdminUser(request, UserRole.ADMIN);
    }

    public SigninResponse signin(SigninRequest request) {
        return authenticateUser(request, UserRole.USER);
    }

    public SigninResponse adminSignin(SigninRequest request) {
        return authenticateUser(request, UserRole.ADMIN);
    }

    private SignupResponse createUser(SignupRequest request, UserRole userRole) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User(
                request.getUsername(),
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getPhoneNumber(),
                request.getAddress(),
                userRole
        );
        User savedUser = userRepository.save(newUser);
        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole);

        return new SignupResponse(bearerToken);
    }

    private SignupResponse createAdminUser(AdminSignupRequest request, UserRole userRole) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User(
                request.getUsername(),
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getPhoneNumber(),
                null, // adminSignup 에서는 address 가 필요하지 않으므로 null로 설정
                userRole
        );
        User savedUser = userRepository.save(newUser);
        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole);

        return new SignupResponse(bearerToken);
    }

    private SigninResponse authenticateUser(SigninRequest request, UserRole requiredRole) {
        User user = findUserByEmail(request.getEmail());

        if (user.getStatus() == Status.DELETED) {
            throw new ApiException(ErrorStatus.NOT_FOUND_USER);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(ErrorStatus.BAD_REQUEST_PASSWORD);
        }

        if (user.getUserRole() != requiredRole) {
            throw new ApiException(ErrorStatus.FORBIDDEN_ACCESS);
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());

        return new SigninResponse(bearerToken);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(ErrorStatus.NOT_FOUND_USER));
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
}
