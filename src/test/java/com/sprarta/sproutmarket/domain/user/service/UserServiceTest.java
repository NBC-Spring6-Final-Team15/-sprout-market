package com.sprarta.sproutmarket.domain.user.service;

import com.sprarta.sproutmarket.domain.user.dto.request.UserChangePasswordRequest;
import com.sprarta.sproutmarket.domain.user.dto.request.UserDeleteRequest;
import com.sprarta.sproutmarket.domain.user.dto.response.UserResponse;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private CustomUserDetails authUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock user data
        user = new User(1L, "username", "email@example.com", "encodedOldPassword", "nickname", "010-1234-5678", "address", UserRole.USER);
        authUser = new CustomUserDetails(user);
    }

    @Test
    void getUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        UserResponse response = userService.getUser(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("email@example.com", response.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUser_Failure_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.getUser(1L));
        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void changePassword_Success() {
        // Given
        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword123", "newPassword456!");

        // 기존 암호화된 비밀번호 설정
        String encodedOldPassword = "$2a$10$k8bk1sFsX0jntulcF2ryJ.TuC.1D5zzbv4D.HbHa1/7BGr3pv6ryy";
        String encodedNewPassword = "$2a$10$CUzO5QICg/F78291f4iy7uYVhzwSMA6jjD2uYkkrvfvPHu7gltHLG";

        user.changePassword(encodedOldPassword); // user 객체의 비밀번호 설정

        // When
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword123", encodedOldPassword)).thenReturn(true); // 기존 비밀번호 확인
        when(passwordEncoder.encode("newPassword456!")).thenReturn(encodedNewPassword); // 새 비밀번호 암호화

        // 비밀번호 변경 실행
        userService.changePassword(authUser, request);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).matches("oldPassword123", encodedOldPassword); // 기존 비밀번호 확인
        verify(passwordEncoder, times(1)).encode("newPassword456!"); // 새 비밀번호 암호화 확인
        assertEquals(encodedNewPassword, user.getPassword()); // 새 비밀번호가 정상적으로 변경되었는지 확인
    }

    @Test
    void changePassword_Failure_WrongOldPassword() {
        // Given
        UserChangePasswordRequest request = new UserChangePasswordRequest("wrongOldPassword", "NewPassword123!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPassword", user.getPassword())).thenReturn(false); // Wrong old password

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.changePassword(authUser, request));
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
        verify(passwordEncoder, times(1)).matches("wrongOldPassword", user.getPassword());
        verify(passwordEncoder, times(0)).encode(anyString()); // New password encoding should not occur
    }

    @Test
    void changePassword_Failure_SameAsOldPassword() {
        // Given
        UserChangePasswordRequest request = new UserChangePasswordRequest("encodedOldPassword", "encodedOldPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("encodedOldPassword", user.getPassword())).thenReturn(true); // Old password matches

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.changePassword(authUser, request));
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
        verify(passwordEncoder, times(1)).matches("encodedOldPassword", user.getPassword());
        verify(passwordEncoder, times(0)).encode(anyString()); // No encoding should happen
    }

    @Test
    void deleteUser_Success() {
        // Given
        UserDeleteRequest request = new UserDeleteRequest("encodedOldPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("encodedOldPassword", user.getPassword())).thenReturn(true); // Correct password

        // When
        userService.deleteUser(authUser, request);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).matches("encodedOldPassword", user.getPassword()); // Check password
        verify(userRepository, times(1)).delete(user); // Verify user deletion
    }

    @Test
    void deleteUser_Failure_WrongPassword() {
        // Given
        UserDeleteRequest request = new UserDeleteRequest("wrongPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false); // Incorrect password

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(authUser, request));
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
        verify(passwordEncoder, times(1)).matches("wrongPassword", user.getPassword()); // Password check
        verify(userRepository, times(0)).delete(any()); // User should not be deleted
    }
}
