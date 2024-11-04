package com.sprarta.sproutmarket.domain.user.service;

import com.sprarta.sproutmarket.domain.areas.service.AdministrativeAreaService;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.profileImage.entity.ProfileImage;
import com.sprarta.sproutmarket.domain.image.profileImage.service.ProfileImageService;
import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
import com.sprarta.sproutmarket.domain.user.dto.request.UserChangePasswordRequest;
import com.sprarta.sproutmarket.domain.user.dto.request.UserDeleteRequest;
import com.sprarta.sproutmarket.domain.user.dto.response.UserAdminResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdministrativeAreaService administrativeAreaService;

    @Mock
    private S3ImageService s3ImageService;
    @Mock
    private ProfileImageService profileImageService;

    @InjectMocks
    private UserService userService;

    private User user;
    private User user2;
    private CustomUserDetails authUser;
    private CustomUserDetails authUser2;
    private MockMultipartFile mockImage;
    private ProfileImage profileImage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock user data
        user = new User("username", "email@example.com", "encodedOldPassword", "nickname", "010-1234-5678", "address", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        user2 = new User("username", "adminEmail@example.com", "encodedOldPassword", "nickname", "010-1234-5678", "address", UserRole.USER);
        ReflectionTestUtils.setField(user2, "id", 2L);
        authUser = new CustomUserDetails(user);
        authUser2 = new CustomUserDetails(user2);
        profileImage = new ProfileImage(user, "https://s3.bucket/profile/test.jpg");
        mockImage = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());
        user2.deactivate();
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
        ApiException exception = assertThrows(ApiException.class, () -> userService.getUser(1L));
        assertEquals(ErrorStatus.NOT_FOUND_USER, exception.getErrorCode());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void changePassword_Success() {
        // Given
        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword123", "newPassword456!");

        String encodedOldPassword = "$2a$10$k8bk1sFsX0jntulcF2ryJ.TuC.1D5zzbv4D.HbHa1/7BGr3pv6ryy";
        String encodedNewPassword = "$2a$10$CUzO5QICg/F78291f4iy7uYVhzwSMA6jjD2uYkkrvfvPHu7gltHLG";

        user.changePassword(encodedOldPassword);

        // When
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword123", encodedOldPassword)).thenReturn(true);
        when(passwordEncoder.encode("newPassword456!")).thenReturn(encodedNewPassword);

        userService.changePassword(authUser, request);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).matches("oldPassword123", encodedOldPassword);
        verify(passwordEncoder, times(1)).encode("newPassword456!");
        assertEquals(encodedNewPassword, user.getPassword());
    }

    @Test
    void changePassword_Failure_WrongOldPassword() {
        // Given
        UserChangePasswordRequest request = new UserChangePasswordRequest("wrongOldPassword", "NewPassword123!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPassword", user.getPassword())).thenReturn(false);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> userService.changePassword(authUser, request));
        assertEquals(ErrorStatus.BAD_REQUEST_PASSWORD, exception.getErrorCode());
        verify(passwordEncoder, times(1)).matches("wrongOldPassword", user.getPassword());
        verify(passwordEncoder, times(0)).encode(anyString());
    }

    @Test
    void changePassword_Failure_SameAsOldPassword() {
        // Given
        UserChangePasswordRequest request = new UserChangePasswordRequest("encodedOldPassword", "encodedOldPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("encodedOldPassword", user.getPassword())).thenReturn(true);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> userService.changePassword(authUser, request));
        assertEquals(ErrorStatus.BAD_REQUEST_NEW_PASSWORD, exception.getErrorCode());
        verify(passwordEncoder, times(1)).matches("encodedOldPassword", user.getPassword());
        verify(passwordEncoder, times(0)).encode(anyString());
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
        assertEquals(Status.DELETED, user.getStatus());
    }

    @Test
    void deleteUser_Failure_WrongPassword() {
        // Given
        UserDeleteRequest request = new UserDeleteRequest("wrongPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> userService.deleteUser(authUser, request));
        assertEquals(ErrorStatus.BAD_REQUEST_PASSWORD, exception.getErrorCode());
        verify(passwordEncoder, times(1)).matches("wrongPassword", user.getPassword());
        verify(userRepository, times(0)).delete(any());
    }

    @Test
    void updateUserAddress_Success() {
        // Given
        double longitude = 126.9784;
        double latitude = 37.5665;
        String newAddress = "서울특별시 종로구";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(administrativeAreaService.getAdministrativeAreaByCoordinates(longitude, latitude)).thenReturn(newAddress);

        // When
        userService.updateUserAddress(1L, longitude, latitude);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(administrativeAreaService, times(1)).getAdministrativeAreaByCoordinates(longitude, latitude);
        assertEquals(newAddress, user.getAddress());
    }

    @Test
    void updateUserAddress_Failure_UserNotFound() {
        // Given
        double longitude = 126.9784;
        double latitude = 37.5665;

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () -> userService.updateUserAddress(1L, longitude, latitude));
        assertEquals(ErrorStatus.NOT_FOUND_USER, exception.getErrorCode());
    }

    @Test
    void 탈퇴_유저_복원_성공() {
        // given
        when(userRepository.findById(authUser2.getId())).thenReturn(Optional.of(user2));

        // when
        userService.activateUser(user2.getId());

        // then
        assertEquals(Status.ACTIVE, user2.getStatus());
        verify(userRepository, times(1)).findById(user2.getId());
    }

    @Test
    void 모든_상태_유저_모두_조회_성공() {
        // given
        Page<User> users = new PageImpl<>(List.of(user, user2));

        when(userRepository.findAll(any(Pageable.class))).thenReturn(users);

        // when
        Page<UserAdminResponse> result = userService.getAllUsers(PageRequest.of(0, 10));

        // then
        assertEquals(2, result.getTotalElements());
        assertEquals("username", result.getContent().get(0).getUsername());
        assertEquals("username", result.getContent().get(1).getUsername());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }
}
