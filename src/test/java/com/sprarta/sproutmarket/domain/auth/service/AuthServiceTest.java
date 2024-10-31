package com.sprarta.sproutmarket.domain.auth.service;

import com.sprarta.sproutmarket.config.JwtUtil;
import com.sprarta.sproutmarket.domain.areas.service.AdministrativeAreaService;
import com.sprarta.sproutmarket.domain.auth.dto.request.AdminSignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.SigninRequest;
import com.sprarta.sproutmarket.domain.auth.dto.request.SignupRequest;
import com.sprarta.sproutmarket.domain.auth.dto.response.SigninResponse;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdministrativeAreaService administrativeAreaService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock 된 adminKey 값을 설정합니다.
        String mockAdminKey = "mock-encrypted-admin-key";
        ReflectionTestUtils.setField(authService, "adminKey", mockAdminKey);
    }

    @Test
    void signupSuccess() {
        // Given
        SignupRequest request = new SignupRequest(
                "username",
                "email@example.com",
                "password",
                "nickname",
                "010-1234-5678",
                126.976889,
                37.575651
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
        when(administrativeAreaService.getAdministrativeAreaByCoordinates(anyDouble(), anyDouble()))
                .thenReturn("서울특별시 종로구");
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
                "adminPassword",
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
        SignupRequest request = new SignupRequest(
                "username",
                "email@example.com",
                "password",
                "nickname",
                "010-1234-5678",
                126.976889,
                37.575651
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        ApiException exception = assertThrows(ApiException.class, () -> authService.signup(request));
        assertEquals(ErrorStatus.BAD_REQUEST_EMAIL, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signinSuccess() {
        SigninRequest request = new SigninRequest("email@example.com", "password");

        User user = new User(
                "username",
                "email@example.com",
                "encodedPassword",
                "nickname",
                "010-1234-5678",
                "서울특별시 종로구",
                UserRole.USER
        );

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole()))
                .thenReturn("jwt-token");

        SigninResponse response = authService.signin(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getBearerToken());
        verify(jwtUtil).createToken(user.getId(), user.getEmail(), user.getUserRole());
    }

    @Test
    void signinFail_UserNotFound() {
        SigninRequest request = new SigninRequest("email@example.com", "password");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> authService.signin(request));
        assertEquals(ErrorStatus.NOT_FOUND_USER, exception.getErrorCode());
    }

    @Test
    void signinFail_WrongPassword() {
        SigninRequest request = new SigninRequest("email@example.com", "password");
        User user = new User("username", "email@example.com", "encodedPassword", "nickname", "010-1234-5678", "서울특별시 종로구", UserRole.USER);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class, () -> authService.signin(request));
        assertEquals(ErrorStatus.BAD_REQUEST_PASSWORD, exception.getErrorCode());
    }

    @Test
    void signinFail_DeletedUser() {
        SigninRequest request = new SigninRequest("email@example.com", "password");
        User user = new User("username", "email@example.com", "encodedPassword", "nickname", "010-1234-5678", "서울특별시 종로구", UserRole.USER);
        user.deactivate();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        ApiException exception = assertThrows(ApiException.class, () -> authService.signin(request));
        assertEquals(ErrorStatus.NOT_FOUND_USER, exception.getErrorCode());
    }
}
