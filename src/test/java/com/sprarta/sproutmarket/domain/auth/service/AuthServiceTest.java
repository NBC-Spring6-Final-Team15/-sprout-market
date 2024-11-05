package com.sprarta.sproutmarket.domain.auth.service;

import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.auth.dto.request.AdminSignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.SignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.response.SignupResponse;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock adminKey value
        ReflectionTestUtils.setField(authService, "adminKey", "mock-encrypted-admin-key");
    }

    @Test
    void signupSuccess() {
        // Given
        SignupRequest request = new SignupRequest(
                "username",
                "email@example.com",
                "ValidPassword1!",
                "nickname",
                "010-1234-5678",
                "서울특별시 마포구 합정동"
        );

        User savedUser = new User(
                "username",
                "email@example.com",
                "encodedPassword",
                "nickname",
                "010-1234-5678",
                "서울특별시 종로구",
                UserRole.USER
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getUserRole()))
                .thenReturn("jwt-token");

        // When
        SignupResponse response = authService.signup(request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getBearerToken());
        verify(jwtUtil).createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getUserRole());
    }

    @Test
    void adminSignupSuccess() {
        // Given
        AdminSignupRequest request = new AdminSignupRequest(
                "adminUsername",
                "admin@example.com",
                "ValidAdminPassword1!",
                "adminNickname",
                "010-1234-5678",
                "mock-encrypted-admin-key"
        );

        User savedUser = new User(
                "adminUsername",
                "admin@example.com",
                "encodedPassword",
                "adminNickname",
                "010-1234-5678",
                null,
                UserRole.ADMIN
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getUserRole()))
                .thenReturn("jwt-token");

        // When
        SignupResponse response = authService.adminSignup(request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getBearerToken());
        verify(jwtUtil).createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getUserRole());
    }


    @Test
    void signupFail_EmailAlreadyExists() {
        // Given
        SignupRequest request = new SignupRequest(
                "username",
                "email@example.com",
                "password",
                "nickname",
                "010-1234-5678",
                "서울특별시 마포구 합정동"
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> authService.signup(request));
        assertEquals(ErrorStatus.BAD_REQUEST_EMAIL, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }
}
