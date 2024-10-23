package com.sprarta.sproutmarket.domain.user.service;

import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_Success() {
        // Given
        User user = new User(
                "username",
                "email@example.com",
                "encodedPassword",
                "nickname",
                "010-1234-5678",
                "address",
                UserRole.USER
        );

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailService.loadUserByUsername("email@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("email@example.com", userDetails.getEmail());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertEquals(UserRole.USER, userDetails.getRole());

        // verify repository method call
        verify(userRepository).findByEmail("email@example.com");
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailService.loadUserByUsername("email@example.com");
        });

        assertEquals("User not found with email: email@example.com", exception.getMessage());

        // verify repository method call
        verify(userRepository).findByEmail("email@example.com");
    }

    @Test
    void loadUserByUsername_DeletedUser() {
        // Given
        User deletedUser = new User(
                "username",
                "email@example.com",
                "encodedPassword",
                "nickname",
                "010-1234-5678",
                "address",
                UserRole.USER
        );
        deletedUser.deactivate();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(deletedUser));

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> {
            customUserDetailService.loadUserByUsername("email@example.com");
        });

        assertEquals(ErrorStatus.BAD_REQUEST_USER, exception.getErrorCode());

        // verify repository method call
        verify(userRepository).findByEmail("email@example.com");
    }
}
