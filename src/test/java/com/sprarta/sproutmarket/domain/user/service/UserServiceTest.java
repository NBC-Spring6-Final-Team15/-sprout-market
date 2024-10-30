package com.sprarta.sproutmarket.domain.user.service;

import com.sprarta.sproutmarket.domain.areas.service.AdministrativeAreaService;
import com.sprarta.sproutmarket.domain.common.entity.Status;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import com.sprarta.sproutmarket.domain.image.entity.Image;
import com.sprarta.sproutmarket.domain.image.service.S3ImageService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

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

    @InjectMocks
    private UserService userService;

    private User user;
    private User user2;
    private CustomUserDetails authUser;
    private CustomUserDetails authUser2;
    private MockMultipartFile mockImage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock user data
        user = new User(1L, "username", "email@example.com", "encodedOldPassword", "nickname", "010-1234-5678", "address", UserRole.USER);
        user2 = new User(2L, "username", "adminEmail@example.com", "encodedOldPassword", "nickname", "010-1234-5678", "address", UserRole.USER);
        authUser = new CustomUserDetails(user);
        authUser2 = new CustomUserDetails(user2);
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
        verify(userRepository, times(1)).save(user);
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
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void 프로필_이미지_업로드_성공() {
        // Given
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(s3ImageService.upload(mockImage, user.getId(), authUser)).thenReturn("https://s3.bucket/profile/test.jpg");

        // When
        String resultUrl = userService.updateProfileImage(authUser, mockImage);

        // Then
        assertEquals("https://s3.bucket/profile/test.jpg", resultUrl);
        assertEquals("https://s3.bucket/profile/test.jpg", user.getProfileImageUrl());
        verify(userRepository, times(1)).findById(authUser.getId());
        verify(s3ImageService, times(1)).upload(mockImage, user.getId(), authUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void 프로필_이미지_삭제_성공() {
        // Given
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        String profileImageUrl = "https://s3.bucket/profile/profileImage.jpg";
        user.updateProfileImage(profileImageUrl);  // 초기 상태로 프로필 이미지 설정

        // When
        userService.deleteProfileImage(authUser);

        // Then
        verify(s3ImageService, times(1)).deleteImageFromS3(profileImageUrl); // S3에서 이미지 삭제 확인
        assertNull(user.getProfileImageUrl());  // 프로필 이미지 URL 이 null 로 변경되었는지 확인
        verify(userRepository, times(1)).findById(authUser.getId());
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
        List<User> users = List.of(user, user2);

        when(userRepository.findAll()).thenReturn(users);

        // when
        List<UserAdminResponse> result = userService.getAllUsers();

        // then
        assertEquals(2, result.size());
        assertEquals("username", result.get(0).getUsername());
        assertEquals("username", result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }
}
