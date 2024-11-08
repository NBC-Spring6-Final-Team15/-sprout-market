package com.sprarta.sproutmarket.domain.image.profileImage.service;

import com.sprarta.sproutmarket.domain.image.dto.response.ImageResponse;
import com.sprarta.sproutmarket.domain.image.profileImage.entity.ProfileImage;
import com.sprarta.sproutmarket.domain.image.profileImage.repository.ProfileImageRepository;
import com.sprarta.sproutmarket.domain.image.s3Image.service.S3ImageService;
import com.sprarta.sproutmarket.domain.item.dto.request.ImageNameRequest;
import com.sprarta.sproutmarket.domain.user.entity.CustomUserDetails;
import com.sprarta.sproutmarket.domain.user.entity.User;
import com.sprarta.sproutmarket.domain.user.enums.UserRole;
import com.sprarta.sproutmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProfileImageServiceTest {
    @InjectMocks
    private ProfileImageService profileImageService;
    @Mock
    private ProfileImageRepository profileImageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private S3ImageService s3ImageService;
    @Mock
    private CustomUserDetails mockAuthUser;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User(
            "testUser",
            "test@test.com",
            "encodedOldPassword",
            "testNickname",
            "010-1234-5678",
            "서울특별시 관악구 신림동",
            UserRole.USER);
        mockAuthUser = new CustomUserDetails(mockUser);
    }

    @Test
    @DisplayName("프로필 이미지 업로드 성공")
    void uploadProfileImage_success() {
        // Given
        ImageNameRequest request = new ImageNameRequest("profileImage.jpg");
        ProfileImage image = new ProfileImage(mockUser, "profileImage.jpg");
        when(userRepository.findByIdAndStatusIsActiveOrElseThrow(mockUser.getId())).thenReturn(mockUser);
        when(profileImageRepository.save(any(ProfileImage.class))).thenReturn(image);

        // when
        ImageResponse response = profileImageService.uploadProfileImage(request, mockAuthUser);

        // then
        assertNotNull(response);
        assertEquals("profileImage.jpg", response.getName());
        verify(userRepository).findByIdAndStatusIsActiveOrElseThrow(mockUser.getId());
        verify(profileImageRepository).save(any(ProfileImage.class));
    }

    @Test
    @DisplayName("프로필 이미지 삭제 성공")
    void deleteProfileImage_success() {
        // given
        ImageNameRequest request = new ImageNameRequest("profileImage.jpg");
        when(userRepository.findByIdAndStatusIsActiveOrElseThrow(mockUser.getId())).thenReturn(mockUser);
        when(profileImageRepository.findByUserOrElseThrow(mockUser)).thenReturn(new ProfileImage(mockUser, "profileImage.jpg"));
        doNothing().when(s3ImageService).deleteImage(anyString(), eq(mockAuthUser));
        doNothing().when(profileImageRepository).deleteByName(anyString());

        // when
        profileImageService.deleteProfileImage(request, mockAuthUser);

        // then
        verify(userRepository).findByIdAndStatusIsActiveOrElseThrow(mockUser.getId());
        verify(profileImageRepository).findByUserOrElseThrow(mockUser);
        verify(s3ImageService).deleteImage("profileImage.jpg", mockAuthUser);
        verify(profileImageRepository).deleteByName("profileImage.jpg");
    }

}